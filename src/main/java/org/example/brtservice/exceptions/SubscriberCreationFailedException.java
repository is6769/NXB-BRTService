package org.example.brtservice.exceptions;

public class SubscriberCreationFailedException extends RuntimeException{
    public SubscriberCreationFailedException(String message) {
        super(message);
    }
}
