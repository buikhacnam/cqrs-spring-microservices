package com.techbank.account.common.events;

import com.techbank.cqrs.cors.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundsWithdrawnEvent extends BaseEvent {
    private double amount;
}
