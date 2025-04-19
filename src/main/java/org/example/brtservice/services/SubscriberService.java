package org.example.brtservice.services;


import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public void createSubscriber(SubscriberDTO subscriberDTO) {
        var tariff = hrsServiceClient.findTariffById(subscriberDTO.tariffId());

    }
}
