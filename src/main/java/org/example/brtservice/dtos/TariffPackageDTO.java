package org.example.brtservice.dtos;

public record TariffPackageDTO(
        Long id,
        Integer priority,
        ServicePackageDTO servicePackage
) {
}
