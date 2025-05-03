package org.example.brtservice.dtos;



import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;

import java.time.LocalDateTime;

public record SubscriberTariffDTO(
        Long id,
        Long subscriberId,
        LocalDateTime cycleStart,
        LocalDateTime cycleEnd,
        TariffDTO tariff
) {
}
