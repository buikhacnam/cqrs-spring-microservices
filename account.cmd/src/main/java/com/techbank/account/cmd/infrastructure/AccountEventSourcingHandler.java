package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.cors.domain.AggregateRoot;
import com.techbank.cqrs.cors.events.BaseEvent;
import com.techbank.cqrs.cors.handlers.EventSourcingHandler;
import com.techbank.cqrs.cors.infrastructure.EventStore;
import com.techbank.cqrs.cors.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 500;
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
            events.sort((a, b) -> Integer.compare(a.getVersion(), b.getVersion()));

            // Process events sequentially with retries
            for(var event: events) {
                publishEventWithRetry(event, MAX_RETRY_ATTEMPTS);

                // Add small delay between events for the same aggregate
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Event republishing was interrupted", e);
                }
            }

            // Add delay between different aggregates
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Event republishing was interrupted", e);
            }
        }
    }

    private void publishEventWithRetry(BaseEvent event, int retriesLeft) {
        try {
            eventProducer.produce("BankAccountEvents", event);
        } catch (Exception e) {
            if (retriesLeft > 0) {
                try {
                    // Wait before retrying
                    Thread.sleep(RETRY_DELAY_MS);
                    publishEventWithRetry(event, retriesLeft - 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Event republishing was interrupted", ie);
                }
            } else {
                throw new RuntimeException("Failed to republish event after " + MAX_RETRY_ATTEMPTS + " retries. Event: " + event, e);
            }
        }
    }
}
