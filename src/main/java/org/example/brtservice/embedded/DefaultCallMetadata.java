package org.example.brtservice.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Встраиваемый класс, представляющий метаданные стандартного звонка.
 * Используется для хранения информации о звонках в других сущностях.
 */
@Data
@Builder
@AllArgsConstructor
public class DefaultCallMetadata {
    private Long id;
    private String callType;
    private String servicedMsisdn;
    private String otherMsisdn;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private Integer durationInMinutes;
    private String otherOperator;
}
