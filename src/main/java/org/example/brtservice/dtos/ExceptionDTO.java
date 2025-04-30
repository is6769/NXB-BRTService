package org.example.brtservice.dtos;

import java.time.LocalDateTime;

public record ExceptionDTO(
        LocalDateTime timestamp,
        Integer status,
        String exceptionType,
        String message,
        String url
) {
}
