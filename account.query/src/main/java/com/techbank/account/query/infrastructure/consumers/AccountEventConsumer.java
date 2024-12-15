package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.infrastructure.handlers.EventHandler;
import com.techbank.cqrs.cors.events.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AccountEventConsumer implements EventConsumer {


    @Autowired
    private EventHandler eventHandler;

    @KafkaListener(topics = "BankAccountEvents", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload BaseEvent event, Acknowledgment ack) {
        try {
            var eventHandlerMethod = eventHandler.getClass().getDeclaredMethod("on", event.getClass());
            eventHandlerMethod.setAccessible(true);
            eventHandlerMethod.invoke(eventHandler, event);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException("Error while consuming event", e);
        }
    }

//    @KafkaListener(topics = "AccountOpenedEvent", groupId = "${spring.kafka.consumer.group-id}")
//    @Override
//    public void consume(AccountOpenedEvent event, Acknowledgment ack) {
//        System.out.println("AccountEventConsumer-consume - "+ event.toString());
//        eventHandler.on(event);
//        ack.acknowledge();
//    }
//
//    @KafkaListener(topics = "FundsDepositedEvent", groupId = "${spring.kafka.consumer.group-id}")
//    @Override
//    public void consume(FundsDepositedEvent event, Acknowledgment ack) {
//        eventHandler.on(event);
//        ack.acknowledge();
//    }
//
//    @KafkaListener(topics = "FundsWithdrawnEvent", groupId = "${spring.kafka.consumer.group-id}")
//    @Override
//    public void consume(FundsWithdrawnEvent event, Acknowledgment ack) {
//        eventHandler.on(event);
//        ack.acknowledge();
//    }
//
//    @KafkaListener(topics = "AccountClosedEvent", groupId = "${spring.kafka.consumer.group-id}")
//    @Override
//    public void consume(AccountClosedEvent event, Acknowledgment ack) {
//        eventHandler.on(event);
//        ack.acknowledge();
//    }
}
