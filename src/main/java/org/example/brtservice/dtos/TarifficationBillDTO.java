package org.example.brtservice.dtos;

import java.math.BigDecimal;

public record TarifficationBillDTO(
        BigDecimal amount,
        String unit,
        Long subscriberId
) {
}
