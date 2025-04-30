package org.example.brtservice.exceptions;

public class NoSuchSubscriberException extends RuntimeException {
    public NoSuchSubscriberException(String message) {
        super(message);
    }
}
