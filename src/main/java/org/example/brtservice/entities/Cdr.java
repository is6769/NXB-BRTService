package org.example.brtservice.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность "Запись данных вызова" (Call Data Record - CDR).
 * <p>
 * Содержит информацию об одном телефонном звонке между двумя абонентами,
 * включая время начала и окончания звонка, типа звонка, а также
 * номера вызывающего и вызываемого абонентов.
 * </p>
 *
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cdr implements Serializable {

    private Long id;

    private String callType;

    private String callerNumber;

    private String calledNumber;

    private LocalDateTime startDateTime;

    private LocalDateTime finishDateTime;


    private ConsumedStatus consumedStatus;
}
