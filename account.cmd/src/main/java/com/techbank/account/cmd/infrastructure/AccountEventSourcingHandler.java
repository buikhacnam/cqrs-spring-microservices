package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.cors.domain.AggregateRoot;
import com.techbank.cqrs.cors.handlers.EventSourcingHandler;
import com.techbank.cqrs.cors.infrastructure.EventStore;
import com.techbank.cqrs.cors.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {
    @Autowired
    private EventStore eventStore;

    @Autowired
    private EventProducer eventProducer;

    @Override
    public void save(AggregateRoot aggregate) {
        System.out.println("AccountEventSourcingHandler - save: " + aggregate.toString());
        eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
        aggregate.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getById(String id) {
        var aggregate = new AccountAggregate();
        var events = eventStore.getEvents(id);
        System.out.println("AccountEventSourcingHandler - getById: " + events.toString());

        if (events != null && !events.isEmpty()) {
            aggregate.replayEvents(events);
            var latestVersion = events.stream().map(x -> x.getVersion()).max(Comparator.naturalOrder());
            aggregate.setVersion(latestVersion.get());
        }
        return aggregate;
    }

    @Override
    public void republishEvents() {
        System.out.println("AccountEventSourcingHandler - republishEvents");
        var aggregateIds = eventStore.getAggregateIds();
        for(var aggregateId: aggregateIds) {
           var aggregate = getById(aggregateId);
           if(aggregate == null) {
               // skip if aggregate is null
               continue;
           }
           var events = eventStore.getEvents(aggregateId);
           for(var event: events) {
               System.out.println("AccountEventSourcingHandler - republishEvents produce: " + event.toString());
                eventProducer.produce(event.getClass().getSimpleName(), event);
           }
        }
    }
}
