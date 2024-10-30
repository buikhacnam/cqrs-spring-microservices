package com.techbank.account.common.events;

import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.cors.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOpenEvent extends BaseEvent {
    private String accountHolder;
    private AccountType accountType;
    private Date creationDate;
    private double openingBalance;
}
