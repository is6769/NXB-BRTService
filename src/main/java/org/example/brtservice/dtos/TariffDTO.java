package org.example.brtservice.dtos;

import org.example.hrsservice.entities.SubscriberTariff;
import org.example.hrsservice.entities.Tariff;
import org.example.hrsservice.entities.TariffPackage;

import java.util.List;

public record TariffDTO(
        Long id,
        String name,
        String description,
        String cycleSize,
        Boolean is_active,
        List<TariffPackageDTO> tariffPackages
) {
}
