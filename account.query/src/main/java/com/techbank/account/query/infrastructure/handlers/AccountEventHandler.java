package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;

    @Autowired
    private AccountRepository accountRepository;

    private void retryOperation(String accountId, Runnable operation) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                operation.run();
                return;
            } catch (Exception e) {
                attempts++;
                if (attempts == MAX_RETRIES) {
                    throw new RuntimeException("Failed to process event after " + MAX_RETRIES + " attempts for account: " + accountId);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
    }


    @Override
    public void on(AccountOpenedEvent event) {
        System.out.println("AccountOpenedEvent on " + event.toString());
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .creationDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .build();
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        retryOperation(event.getId(), () -> {
            var bankAccount = accountRepository.findById(event.getId());
            if (bankAccount.isEmpty()) {
                throw new RuntimeException("Account not found - possible race condition");
            }
            var currentBalance = bankAccount.get().getBalance();
            var latestBalance = currentBalance + event.getAmount();
            bankAccount.get().setBalance(latestBalance);
            accountRepository.save(bankAccount.get());
        });
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        retryOperation(event.getId(), () -> {
            var bankAccount = accountRepository.findById(event.getId());
            if (bankAccount.isEmpty()) {
                throw new RuntimeException("Account not found - possible race condition");
            }
            var currentBalance = bankAccount.get().getBalance();
            var latestBalance = currentBalance - event.getAmount();
            bankAccount.get().setBalance(latestBalance);
            accountRepository.save(bankAccount.get());
        });
    }

    @Override
    public void on(AccountClosedEvent event) {
        accountRepository.deleteById(event.getId());
    }
}
