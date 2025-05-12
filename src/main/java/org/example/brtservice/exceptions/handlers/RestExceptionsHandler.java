package org.example.brtservice.exceptions.handlers;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.ExceptionDTO;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberAlreadyExistsException;
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

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает специфичные исключения приложения и HTTP клиентские/серверные ошибки.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionsHandler {

    private final HRSServiceClient hrsServiceClient;

    public RestExceptionsHandler(HRSServiceClient hrsServiceClient) {
        this.hrsServiceClient = hrsServiceClient;
    }

    /**
     * Обрабатывает исключение {@link NoSuchSubscriberException}.
     * Возвращает HTTP статус 404 (Not Found).
     * @param request HTTP запрос.
     * @param ex перехваченное исключение.
     * @return {@link ExceptionDTO} с деталями ошибки.
     */
    @ExceptionHandler(exception = NoSuchSubscriberException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNoSuchSubscriberException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                hrsServiceClient.getSystemDatetime(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link SubscriberCreationFailedException}.
     * Возвращает HTTP статус 500 (Internal Server Error).
     * @param request HTTP запрос.
     * @param ex перехваченное исключение.
     * @return {@link ExceptionDTO} с деталями ошибки.
     */
    @ExceptionHandler(exception = SubscriberCreationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleSubscriberCreationFailedException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                hrsServiceClient.getSystemDatetime(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link SubscriberAlreadyExistsException}.
     * Возвращает HTTP статус 400 (Bad Request).
     * @param request HTTP запрос.
     * @param ex перехваченное исключение.
     * @return {@link ExceptionDTO} с деталями ошибки.
     */
    @ExceptionHandler(exception = SubscriberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleSubscriberAlreadyExistsException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                hrsServiceClient.getSystemDatetime(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключения {@link HttpClientErrorException} и {@link HttpServerErrorException}.
     * Проксирует оригинальный ответ от нижележащего сервиса, включая статус, заголовки и тело ответа.
     * @param ex перехваченное HTTP исключение.
     * @return {@link ResponseEntity} с оригинальными данными ошибки.
     */
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
