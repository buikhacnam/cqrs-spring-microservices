package com.techbank.cqrs.cors.domain;

import com.techbank.cqrs.cors.events.BaseEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    protected String id;
    private int version = -1;
//    The changes list serves as a temporary holding area for new events until they can be persisted to the event store. This is different from historical events which are replayed with isNewEvent=false and don't get added to the changes list.
//    This pattern ensures:
//    All state changes are tracked as events
//    New events are collected before being persisted
//    Events are only persisted once
//    The aggregate's state remains consistent with its events
    private final List<BaseEvent> changes = new ArrayList<>();
    private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<BaseEvent> getUncommittedChanges() {
        logger.info("getUncommittedChanges: " + changes.stream().map(x -> x.getClass().getName()).reduce("", (x, y) -> x + ", " + y));
        return changes;
    }

    public void markChangesAsCommitted() {
        changes.clear();
    }

    protected void applyChange(BaseEvent event, Boolean isNewEvent) {
        logger.info("Applying change: " + event);
        try {
            //  Gets the runtime class of 'this' object (e.g., AccountAggregate)
            // Searches for a method in this class with:
            // - Method name "apply"
            // - Parameter type matching the event's class (e.g., AccountOpenedEvent)
            var method = getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (NoSuchMethodException e) {
            logger.log(Level.WARNING, "No apply method found for event " + event.getClass().getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error applying event " + event.getClass().getName(), e);
        } finally {
            if(isNewEvent) {
                changes.add(event);
            }
            logger.info("changes list: " + changes.stream().map(x -> x.getClass().getName()).reduce("", (x, y) -> x + ", " + y));
        }
    }

    public void raiseEvent(BaseEvent event) {
        applyChange(event, true);
    }

    public void replayEvents(Iterable<BaseEvent> events) {
        for(var event : events) {
            applyChange(event, false);
        }
    }
}
