package org.example.brtservice.dtos;


public record ExceptionDTO(
        Integer status,
        String exceptionType,
        String message,
        String url
) {
}
