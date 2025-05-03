package org.example.brtservice.dtos;

import java.math.BigDecimal;

public record PackageRuleDTO(
        Long id,
        String ruleType,
        BigDecimal value,
        String unit,
        ConditionNode condition
) {
}
