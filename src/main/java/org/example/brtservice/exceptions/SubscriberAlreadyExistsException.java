package org.example.brtservice.exceptions;

public class SubscriberAlreadyExistsException extends RuntimeException {
    public SubscriberAlreadyExistsException(String message) {
        super(message);
    }
}
