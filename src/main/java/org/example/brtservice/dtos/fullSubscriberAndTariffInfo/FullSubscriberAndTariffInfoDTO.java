package org.example.brtservice.dtos.fullSubscriberAndTariffInfo;

import org.example.brtservice.entities.Subscriber;

public record FullSubscriberAndTariffInfoDTO(
        Subscriber subscriber,
        TariffDTO tariff
) {
}
