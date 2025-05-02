package org.example.brtservice.dtos;



import java.time.LocalDateTime;

public record SubscriberTariffDTO(
        Long id,
        Long subscriberId,
        LocalDateTime cycleStart,
        LocalDateTime cycleEnd,
        TariffDTO tariff
) {
}
