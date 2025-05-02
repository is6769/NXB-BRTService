package org.example.brtservice.services;


import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberCreationFailedException;
import org.example.brtservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для работы с абонентами.
 * Предоставляет методы для поиска и проверки существования абонентов.
 */
@Slf4j
@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final HRSServiceClient hrsServiceClient;

    public SubscriberService(SubscriberRepository subscriberRepository, HRSServiceClient hrsServiceClient) {
        this.subscriberRepository = subscriberRepository;
        this.hrsServiceClient = hrsServiceClient;
    }

    /**
     * Получает список всех абонентов.
     *
     * @return Список всех абонентов
     */
    public List<Subscriber> findAll(){
        return subscriberRepository.findAll();
    }


    public void createSubscriber(SubscriberDTO subscriberDTO) {
        LocalDateTime systemDatetime=hrsServiceClient.getSystemDatetime();
        Subscriber newSubscriber = subscriberDTO.toEntity();
        newSubscriber.setRegisteredAt(systemDatetime);
        newSubscriber = subscriberRepository.save(newSubscriber);

        if (Objects.isNull(subscriberRepository.findSubscriberById(newSubscriber.getId()))) throw new SubscriberCreationFailedException("Cant set tariff for subscriber. Subscriber was not saved.");

        if (Objects.nonNull(subscriberDTO.tariffId())) hrsServiceClient.setTariffForSubscriber(newSubscriber.getId(),newSubscriber.getTariffId(),systemDatetime);

    }
    
    

    public Optional<Subscriber> findSubscriberByMsisdn(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn);
    }

    //TODO make atomic updates
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addAmountToBalance(Long subscriberId, BigDecimal chargeAmount){
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId);
        subscriber.setBalance(subscriber.getBalance().add(chargeAmount));
        subscriberRepository.save(subscriber);
    }

    //TODO make atomic updates
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void subtractAmountFromBalance(Long subscriberId, BigDecimal chargeAmount){
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId);
        if (Objects.isNull(subscriber)) throw new NoSuchSubscriberException("Cant subtract such amount from balance. No such subscriber present.");
        subscriber.setBalance(subscriber.getBalance().subtract(chargeAmount));
        subscriberRepository.save(subscriber);
    }

    public boolean isSubscriberPresent(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn).isPresent();
    }

    public void setTariffForSubscriber(Long subscriberId, Long tariffId) {
        LocalDateTime systemDatetime=hrsServiceClient.getSystemDatetime();
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId);
        subscriber.setTariffId(tariffId);
        subscriberRepository.save(subscriber);
        hrsServiceClient.setTariffForSubscriber(subscriberId,tariffId,systemDatetime);

    }

    public String getSubscriberAndTariffInfo(Long subscriberId) {
        subscriberRepository.findSubscriberById(subscriberId);
        hrsServiceClient.getSubscriberTariffInfo(subscriberId);
        return null;
    }
}
