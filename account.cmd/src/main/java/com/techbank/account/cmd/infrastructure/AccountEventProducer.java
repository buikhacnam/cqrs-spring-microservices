package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.cors.events.BaseEvent;
import com.techbank.cqrs.cors.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountEventProducer implements EventProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(String topic, BaseEvent event) {
        System.out.println("AccountEventProducer - produce: " + topic + " - " + event.toString());
        //AccountEventProducer - produce: AccountOpenedEvent - AccountOpenedEvent(accountHolder=Casey1, accountType=SAVINGS, createdDate=Fri Nov 29 18:42:09 ICT 2024, openingBalance=55.0)
        this.kafkaTemplate.send(topic, event);
    }
}

