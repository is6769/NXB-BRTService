package org.example.brtservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность Абонент, представляющая пользователя телекоммуникационных услуг.
 * <p>
 * Абонент идентифицируется по уникальному номеру телефона (MSISDN).
 * Эта сущность является основной для учета и генерации записей о звонках.
 * </p>
 *
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
@Entity
@Table(name = "subscribers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {

    /**
     * Уникальный идентификатор абонента.
     * <p>
     * Автоматически генерируется базой данных при сохранении.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер мобильного телефона абонента в международном формате (MSISDN).
     * <p>
     * Это поле должно быть уникальным в системе и не может быть пустым.
     * Используется для идентификации абонента при обработке звонков.
     * </p>
     */
    @Column(name = "msisdn", unique = true, nullable = false)
    private String msisdn;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "tariff_id")
    private Long tariffId;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    public void prePersist(){
        if (Objects.isNull(this.balance)) balance = new BigDecimal(100);
    }

}
