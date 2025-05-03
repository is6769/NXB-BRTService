package org.example.brtservice.dtos.fullSubscriberAndTariffInfo;

public record TariffPackageDTO(
        Long id,
        Integer priority,
        ServicePackageDTO servicePackage
) {
}
