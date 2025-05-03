package org.example.brtservice.dtos.fullSubscriberAndTariffInfo;


import java.util.List;

public record ConditionNodeDTO(
        String type,
        String field,
        String operator,
        String value,
        List<ConditionNodeDTO> conditions
) {

//    private String type;
//
//    private String field;
//
//    private String operator;
//
//    private String value;
//
//    private List<ConditionNodeDTO> conditions;
}
