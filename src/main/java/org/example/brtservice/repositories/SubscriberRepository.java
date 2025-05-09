package org.example.brtservice.repositories;


import jakarta.persistence.LockModeType;
import org.example.brtservice.entities.Subscriber;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Абонент.
 * <p>
 * Обеспечивает доступ к базе данных для выполнения операций
 * создания, чтения, обновления и удаления (CRUD) абонентов.
 * Также предоставляет дополнительные методы поиска абонентов.
 * </p>
 *
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    /**
     * Поиск всех абонентов, соответствующих примеру.
     *
     * @param example Пример абонента для поиска соответствий
     * @param <S> Тип, производный от сущности Subscriber
     * @return Список абонентов, соответствующих примеру
     */
    @Override
    <S extends Subscriber> List<S> findAll(Example<S> example);

    /**
     * Поиск абонента по номеру телефона (MSISDN).
     * <p>
     * Метод ищет абонента по точному совпадению номера телефона.
     * </p>
     *
     * @param msisdn Номер мобильного телефона абонента
     * @return Optional, содержащий найденного абонента, или пустой Optional, если абонент не найден
     */
    Optional<Subscriber> findSubscriberByMsisdn(String msisdn);

    Optional<Subscriber> findSubscriberById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Subscriber s where s.id=:id")
    Optional<Subscriber> findSubscriberByIdWithLock(Long id);

//    @Query("UPDATE Subscriber s set s.balance=s.balance+:amount where s.id=:subscriberId")
//    @Modifying(clearAutomatically=true, flushAutomatically=true)
//    void atomicAddToBalance(Long subscriberId,BigDecimal amount);
//
//    @Query("UPDATE Subscriber s set s.balance=s.balance-:amount where s.id=:subscriberId")
//    @Modifying(clearAutomatically=true, flushAutomatically=true)
//    void atomicSubtractFromBalance(Long subscriberId,BigDecimal amount);
}
