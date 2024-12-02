package com.techbank.cqrs.cors.handlers;

import com.techbank.cqrs.cors.domain.AggregateRoot;

public interface EventSourcingHandler<T> {
    void save(AggregateRoot aggregate);

    T getById(String id);

    void republishEvents();
}
