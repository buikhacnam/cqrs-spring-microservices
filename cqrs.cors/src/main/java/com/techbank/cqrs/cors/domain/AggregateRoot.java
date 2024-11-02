package com.techbank.cqrs.cors.domain;

import com.techbank.cqrs.cors.events.BaseEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    protected String id;
    private int version = -1;

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
        return changes;
    }

    public void markChangesAsCommitted() {
        changes.clear();
    }

    protected void applyChange(BaseEvent event, Boolean isNewEvent) {
        logger.info("Applying change: " + event);
        try {
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
