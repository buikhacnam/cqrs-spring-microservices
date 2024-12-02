package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.cqrs.cors.events.BaseEvent;
import com.techbank.cqrs.cors.events.EventModel;
import com.techbank.cqrs.cors.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.cors.exceptions.ConcurrencyException;
import com.techbank.cqrs.cors.infrastructure.EventStore;
import com.techbank.cqrs.cors.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventStoreRepository eventStoreRepository;
    @Override
    public void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        System.out.println("AccountEventStore - saveEvents: " + aggregateId + " - " + events.toString() + " - " + expectedVersion);
        var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if (expectedVersion != -1 && eventStream.get(eventStream.size() - 1).getVersion() != expectedVersion) {
            // != -1 means this is not a new aggregate && the last event version is not equal to the expected version
            throw new ConcurrencyException();
        }
        var version = expectedVersion;
        for (var event: events) {
            version++; //so new aggregate will start with version 0 (initial version is -1)
            event.setVersion(version);
            var eventModel = EventModel.builder()
                    .timeStamp(new Date())
                    .aggregateIdentifier(aggregateId)
                    .aggregateType(AccountAggregate.class.getTypeName())
                    .version(version)
                    .eventType(event.getClass().getTypeName())
                    .eventData(event)
                    .build();
            var persistedEvent = eventStoreRepository.save(eventModel);
            if (!persistedEvent.getId().isEmpty()) {
                System.out.println("AccountEventStore - saveEvents produce: " + event.toString());
                eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
        System.out.println("saveEvents - end");
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if (eventStream == null || eventStream.isEmpty()) {
            throw new AggregateNotFoundException("Incorrect account ID provided!");
        }
        return eventStream.stream().map(x -> x.getEventData()).collect(Collectors.toList());
    }

    @Override
    public List<String> getAggregateIds() {
        var eventStream = eventStoreRepository.findAll();
        if(eventStream == null || eventStream.isEmpty()) {
            throw new AggregateNotFoundException("could not retrieve aggregate IDs");
        }
        return eventStream.stream()
            .map(x -> x.getAggregateIdentifier())
            .distinct()
            .collect(Collectors.toList());
    }
}