package org.example.brtservice.dtos;

import org.example.brtservice.entities.Subscriber;

public record FullSubscriberAndTariffInfoDTO(
        Subscriber subscriber,
        TariffDTO tariff
) {
}
