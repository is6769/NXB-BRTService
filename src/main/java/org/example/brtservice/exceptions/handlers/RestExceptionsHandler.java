package org.example.brtservice.exceptions.handlers;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.ExceptionDTO;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberCreationFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionsHandler {

    @ExceptionHandler(exception = NoSuchSubscriberException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNoSuchSubscriberException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
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
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    @ExceptionHandler(exception = {HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<byte[]> handleHttpClientExceptions(HttpStatusCodeException ex){
        HttpHeaders originalHeaders = ex.getResponseHeaders();
        HttpHeaders newHeaders = new HttpHeaders();
        if (originalHeaders != null) {
            originalHeaders.forEach((key, value) -> {
                if (!key.equalsIgnoreCase(HttpHeaders.TRANSFER_ENCODING) &&
                    !key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                    newHeaders.put(key, value);
                }
            });
            if (originalHeaders.getContentType() != null) {
                newHeaders.setContentType(originalHeaders.getContentType());
            }
        }

        return ResponseEntity
                .status(ex.getStatusCode())
                .headers(newHeaders)
                .body(ex.getResponseBodyAsByteArray());
    }
}
