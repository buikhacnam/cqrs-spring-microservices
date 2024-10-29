package com.techbank.account.cmd.api.commands;

import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.cors.commands.BaseCommand;
import lombok.Data;

@Data
public class OpenAccountCommand extends BaseCommand {
    private String accountHolder;
    private AccountType accountType;
    private double openingBalance;
}
