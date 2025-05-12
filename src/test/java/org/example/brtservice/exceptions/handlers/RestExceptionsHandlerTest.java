package org.example.brtservice.exceptions.handlers;

import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.ExceptionDTO;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberAlreadyExistsException;
import org.example.brtservice.exceptions.SubscriberCreationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link RestExceptionsHandler}.
 * Проверяют корректность обработки различных REST исключений.
 */
@ExtendWith(MockitoExtension.class)
class RestExceptionsHandlerTest {

    @Mock
    private HRSServiceClient hrsServiceClient;

    @InjectMocks
    private RestExceptionsHandler restExceptionsHandler;

    private MockHttpServletRequest request;
    private final LocalDateTime testTime = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRequestURI("/test");

    }

    @Test
    void handleNoSuchSubscriberException_returnsNotFoundStatus() {
        String errorMessage = "Subscriber not found";
        NoSuchSubscriberException exception = new NoSuchSubscriberException(errorMessage);
        when(hrsServiceClient.getSystemDatetime()).thenReturn(testTime);
        
        ExceptionDTO result = restExceptionsHandler.handleNoSuchSubscriberException(request, exception);
        
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.status());
        assertEquals("NOT_FOUND", result.exceptionType());
        assertEquals(errorMessage, result.message());
        assertEquals(testTime, result.timestamp());
        assertEquals("http://localhost/test", result.url());
    }

    @Test
    void handleSubscriberCreationFailedException_returnsInternalServerErrorStatus() {
        String errorMessage = "Failed to create subscriber";
        SubscriberCreationFailedException exception = new SubscriberCreationFailedException(errorMessage);
        when(hrsServiceClient.getSystemDatetime()).thenReturn(testTime);
        
        ExceptionDTO result = restExceptionsHandler.handleSubscriberCreationFailedException(request, exception);
        
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.status());
        assertEquals("INTERNAL_SERVER_ERROR", result.exceptionType());
        assertEquals(errorMessage, result.message());
        assertEquals(testTime, result.timestamp());
        assertEquals("http://localhost/test", result.url());
    }

    @Test
    void handleSubscriberAlreadyExistsException_returnsBadRequestStatus() {
        String errorMessage = "Subscriber already exists";
        SubscriberAlreadyExistsException exception = new SubscriberAlreadyExistsException(errorMessage);
        when(hrsServiceClient.getSystemDatetime()).thenReturn(testTime);

        ExceptionDTO result = restExceptionsHandler.handleSubscriberAlreadyExistsException(request, exception);
        
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.status());
        assertEquals("BAD_REQUEST", result.exceptionType());
        assertEquals(errorMessage, result.message());
        assertEquals(testTime, result.timestamp());
        assertEquals("http://localhost/test", result.url());
    }

    @Test
    void handleHttpClientExceptions_returnsOriginalResponseStatus() {
        byte[] responseBody = "Error message".getBytes();
        
        HttpClientErrorException clientException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad request", null, responseBody, null);
        
        ResponseEntity<byte[]> result = restExceptionsHandler.handleHttpClientExceptions(clientException);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertArrayEquals(responseBody, result.getBody());
    }

    @Test
    void handleHttpServerExceptions_returnsOriginalResponseStatus() {
        byte[] responseBody = "Server error".getBytes();
        
        HttpServerErrorException serverException = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null, responseBody, null);
        
        ResponseEntity<byte[]> result = restExceptionsHandler.handleHttpClientExceptions(serverException);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertArrayEquals(responseBody, result.getBody());
    }
}
