package com.techbank.cqrs.cors.producers;

import com.techbank.cqrs.cors.events.BaseEvent;

public interface EventProducer {
    void produce(String topic, BaseEvent event);

}
