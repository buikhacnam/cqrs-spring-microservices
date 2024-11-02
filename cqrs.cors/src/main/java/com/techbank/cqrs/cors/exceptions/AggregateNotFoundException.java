package com.techbank.cqrs.cors.exceptions;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException(String message) {
        super(message);
    }
}
