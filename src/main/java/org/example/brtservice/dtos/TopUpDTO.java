package org.example.brtservice.dtos;

import java.math.BigDecimal;

public record TopUpDTO(
        BigDecimal amount,
        String unit
) {
}
