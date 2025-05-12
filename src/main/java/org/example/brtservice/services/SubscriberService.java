package org.example.brtservice.services;


import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.FullSubscriberAndTariffInfoDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberAlreadyExistsException;
import org.example.brtservice.exceptions.SubscriberCreationFailedException;
import org.example.brtservice.repositories.SubscriberRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для работы с абонентами.
 * Предоставляет методы для поиска и проверки существования абонентов.
 */
@Slf4j
@Service
public class SubscriberService {

    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_EXCHANGE_NAME}")
    private String SUBSCRIBER_CREATED_EXCHANGE_NAME;

    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_ROUTING_KEY}")
    private String SUBSCRIBER_CREATED_ROUTING_KEY;

    private final SubscriberRepository subscriberRepository;
    private final HRSServiceClient hrsServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public SubscriberService(SubscriberRepository subscriberRepository, HRSServiceClient hrsServiceClient, RabbitTemplate rabbitTemplate) {
        this.subscriberRepository = subscriberRepository;
        this.hrsServiceClient = hrsServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Получает список всех абонентов.
     *
     * @return Список всех абонентов
     */
    public List<Subscriber> findAll(){
        return subscriberRepository.findAll();
    }


    public Subscriber createSubscriber(SubscriberDTO subscriberDTO) {
        LocalDateTime systemDatetime=hrsServiceClient.getSystemDatetime();

        Subscriber newSubscriber = subscriberDTO.toEntity();

        String msisdn = newSubscriber.getMsisdn();
        if (findSubscriberByMsisdn(msisdn).isPresent()) throw new SubscriberAlreadyExistsException("Subscriber with such msisdn: %s already exists.".formatted(msisdn));
        Long tariffId = newSubscriber.getTariffId();
        if (Objects.nonNull(tariffId)) hrsServiceClient.getTariffInfo(tariffId);//to check that tariff really exists


        newSubscriber.setRegisteredAt(systemDatetime);
        newSubscriber = subscriberRepository.save(newSubscriber);
        subscriberRepository.findSubscriberById(newSubscriber.getId()).orElseThrow(()->new SubscriberCreationFailedException("Cant create subscriber."));

        rabbitTemplate.convertAndSend(SUBSCRIBER_CREATED_EXCHANGE_NAME,SUBSCRIBER_CREATED_ROUTING_KEY, Map.of("subscriberId",newSubscriber.getId(),"msisdn",msisdn));

        if (Objects.nonNull(subscriberDTO.tariffId())) hrsServiceClient.setTariffForSubscriber(newSubscriber.getId(),tariffId,systemDatetime);

        return newSubscriber;
    }
    
    

    public Optional<Subscriber> findSubscriberByMsisdn(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn);
    }


    @Transactional
    public void addAmountToBalance(Long subscriberId, BigDecimal chargeAmount){
        Subscriber subscriber = subscriberRepository.findSubscriberByIdWithLock(subscriberId).orElseThrow(()->new NoSuchSubscriberException("Cant add such amount to balance. No such subscriber present."));
        subscriber.setBalance(subscriber.getBalance().add(chargeAmount));
        subscriberRepository.save(subscriber);
    }


    @Transactional
    public void subtractAmountFromBalance(Long subscriberId, BigDecimal chargeAmount){
        Subscriber subscriber = subscriberRepository.findSubscriberByIdWithLock(subscriberId).orElseThrow(()->new NoSuchSubscriberException("Cant subtract such amount from balance. No such subscriber present."));
        subscriber.setBalance(subscriber.getBalance().subtract(chargeAmount));
        subscriberRepository.save(subscriber);
    }

    public boolean isSubscriberPresent(String msisdn){
        return subscriberRepository.findSubscriberByMsisdn(msisdn).isPresent();
    }

    public void setTariffForSubscriber(Long subscriberId, Long tariffId) {
        LocalDateTime systemDatetime=hrsServiceClient.getSystemDatetime();
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId).orElseThrow(()->new NoSuchSubscriberException("Cant set such tariff for subscriber. No such subscriber present."));

        hrsServiceClient.getTariffInfo(tariffId);//to check that tariff really exists

        subscriber.setTariffId(tariffId);
        subscriberRepository.save(subscriber);
        hrsServiceClient.setTariffForSubscriber(subscriberId,tariffId,systemDatetime);

    }

    public FullSubscriberAndTariffInfoDTO getSubscriberAndTariffInfo(Long subscriberId) {
        Subscriber subscriber = subscriberRepository.findSubscriberById(subscriberId).orElseThrow(()->new NoSuchSubscriberException("Cant get info about subscriber. No such subscriber present."));
        TariffDTO tariffDTO = (Objects.nonNull(subscriber.getTariffId())) ? hrsServiceClient.getTariffInfoBySubscriberId(subscriberId) : null;
        return new FullSubscriberAndTariffInfoDTO(subscriber,tariffDTO);

    }
}
