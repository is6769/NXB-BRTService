package org.example.brtservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Entity
@Table(name = "cdrs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cdr {

    /**
     * Уникальный идентификатор записи CDR.
     * <p>
     * Автоматически генерируется базой данных при сохранении.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип вызова.
     * <p>
     * 01 - входящий вызов, 02 - исходящий вызов.
     * </p>
     */
    @Column(name = "call_type", nullable = false)
    private String callType;


    @Column(name = "serviced_msisdn", nullable = false)
    private String servicedMsisdn;

    @Column(name = "other_msisdn", nullable = false)
    private String otherMsisdn;

    /**
     * Дата и время начала вызова.
     */
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    /**
     * Дата и время окончания вызова.
     */
    @Column(name = "finish_date_time", nullable = false)
    private LocalDateTime finishDateTime;
}
