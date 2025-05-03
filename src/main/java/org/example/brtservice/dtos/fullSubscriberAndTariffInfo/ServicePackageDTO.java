package org.example.brtservice.dtos.fullSubscriberAndTariffInfo;

import java.util.List;

public record ServicePackageDTO(
        Long id,
        String name,
        String description,
        String serviceType,
        List<PackageRuleDTO> packageRules
        //List<SubscriberPackageUsage> subscriberPackageUsages
) {
}
