package com.techbank.account.common.events;

import com.techbank.cqrs.cors.events.BaseEvent;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class AccountClosedEvent extends BaseEvent {

}
