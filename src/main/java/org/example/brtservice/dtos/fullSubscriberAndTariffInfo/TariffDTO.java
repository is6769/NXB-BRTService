package org.example.brtservice.dtos.fullSubscriberAndTariffInfo;

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
