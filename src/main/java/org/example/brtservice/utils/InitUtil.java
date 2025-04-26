package org.example.brtservice.utils;

import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.services.SubscriberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class InitUtil implements CommandLineRunner{

    private final SubscriberService subscriberService;

    public InitUtil(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<SubscriberDTO> subscriberDTOs = List.of(
                new SubscriberDTO("79000000001", "Иван", "Иванович", "Петров", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000002", "Анна", "Сергеевна", "Смирнова", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000003", "Дмитрий", "Владимирович", "Кузнецов", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000004", "Елена", "Андреевна", "Волкова", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000005", "Алексей", "Дмитриевич", "Фёдоров", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000006", "Ольга", "Олеговна", "Николаева", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000007", "Сергей", "Петрович", "Морозов", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000008", "Мария", "Александровна", "Павлова", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000009", "Андрей", "Игоревич", "Соколов", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79000000010", "Наталья", "Викторовна", "Орлова", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79111111111", "Павел", "Михайлович", "Беляев", 1L, new BigDecimal("100.00")),
                new SubscriberDTO("79222222222", "Екатерина", "Дмитриевна", "Лебедева", 2L, new BigDecimal("100.00")),
                new SubscriberDTO("79999999999", "Виктор", "Сергеевич", "Гусев", 1L, new BigDecimal("100.00"))
        );
        List<Subscriber> subscribers = subscriberService.findAll();
        if (subscribers.isEmpty()){
            subscriberDTOs.forEach(subscriberService::createSubscriber);
        }

    }
}
