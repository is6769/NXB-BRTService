package org.example.brtservice.exceptions.handlers;


import jakarta.servlet.http.HttpServletRequest;
import org.example.brtservice.dtos.ExceptionDTO;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberCreationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(exception = NoSuchSubscriberException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNoSuchSubscriberException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    @ExceptionHandler(exception = SubscriberCreationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleSubscriberCreationFailedException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }
}
