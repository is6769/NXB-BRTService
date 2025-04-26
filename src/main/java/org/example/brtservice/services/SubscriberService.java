package org.example.brtservice.services;


import jakarta.transaction.Transactional;
import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с абонентами.
 * Предоставляет методы для поиска и проверки существования абонентов.
 */
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


    @Transactional
    public void createSubscriber(SubscriberDTO subscriberDTO) {
        LocalDateTime systemDatetime=hrsServiceClient.getSystemDatetime();
        Subscriber newSubscriber = subscriberDTO.toEntity();
        newSubscriber.setRegisteredAt(systemDatetime);
        newSubscriber = subscriberRepository.save(newSubscriber);
        hrsServiceClient.setTariffForSubscriber(newSubscriber.getId(),newSubscriber.getTariffId(),systemDatetime);

    }

    public Optional<Subscriber> findSubscriberByMsisdn(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn);
    }

    public void addAmountToBalance(BigDecimal chargeAmount){

    }

//    public void subtractAmountFromBalance(String msisdn, BigDecimal chargeAmount){
//        Subscriber subscriber = subscriberRepository.findSubscriberByMsisdn(msisdn).orElseThrow(RuntimeException::new);
//        subscriber.setBalance(subscriber.getBalance().subtract(chargeAmount));
//    }
    public void subtractAmountFromBalance(Long subscriberId, BigDecimal chargeAmount){
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId);
        subscriber.setBalance(subscriber.getBalance().subtract(chargeAmount));
        subscriberRepository.save(subscriber);
    }

    public boolean isSubscriberPresent(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn).isPresent();
    }
}
