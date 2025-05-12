package org.example.brtservice.services;

import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.embedded.DefaultCallMetadata;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.repositories.CdrRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Сервис для работы с CDR (Call Detail Record).
 * Предоставляет методы для сохранения CDR и преобразования их в DTO с метаданными звонка.
 */
@Service
public class CdrService {

    private final CdrRepository cdrRepository;
    private final SubscriberService subscriberService;

    public CdrService(CdrRepository cdrRepository, SubscriberService subscriberService) {
        this.cdrRepository = cdrRepository;
        this.subscriberService = subscriberService;
    }

    public void save(Cdr cdr){
        cdrRepository.save(cdr);
    }

    /**
     * Преобразует {@link Cdr} в {@link CallWithDefaultMetadataDTO}.
     * Определяет, является ли второй участник звонка абонентом "Ромашки",
     * и вычисляет продолжительность звонка.
     *
     * @param cdr объект {@link Cdr} для преобразования.
     * @return {@link CallWithDefaultMetadataDTO} с информацией о звонке и метаданными.
     * @throws NoSuchSubscriberException если обслуживаемый абонент (servicedMsisdn) не найден.
     */
    public CallWithDefaultMetadataDTO convertToCallWithDefaultMetadataDTO(Cdr cdr) {

        Subscriber subscriber = subscriberService.findSubscriberByMsisdn(cdr.getServicedMsisdn()).orElseThrow(()->new NoSuchSubscriberException("Cant find serviced subscriber by msisdn"));
        boolean isOtherOur = subscriberService.isSubscriberPresent(cdr.getOtherMsisdn());

        DefaultCallMetadata defaultCallMetadata = new DefaultCallMetadata(
                cdr.getId(),
                cdr.getCallType(),
                cdr.getServicedMsisdn(),
                cdr.getOtherMsisdn(),
                cdr.getStartDateTime(),
                cdr.getFinishDateTime(),
                calculateDurationInMinutes(cdr.getStartDateTime(),cdr.getFinishDateTime()),
                (isOtherOur) ? "Ромашка" : "Other"
        );

        return new CallWithDefaultMetadataDTO(subscriber.getId(), defaultCallMetadata);
    }

    /**
     * Вычисляет продолжительность звонка в минутах, округляя в большую сторону.
     * @param start дата и время начала звонка.
     * @param finish дата и время окончания звонка.
     * @return продолжительность звонка в минутах.
     */
    private Integer calculateDurationInMinutes(LocalDateTime start, LocalDateTime finish){
        return (int) Math.ceil(Duration.between(start, finish).toSeconds()/60.0);
    }
}
