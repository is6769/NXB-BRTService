package org.example.brtservice.controllers;

import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.dtos.TopUpDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.FullSubscriberAndTariffInfoDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.services.SubscriberService;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для управления операциями, связанными с абонентами.
 * Предоставляет API для создания, пополнения баланса, смены тарифа и получения информации об абонентах.
 */
@RestController
public class SubscribersRestController {

    private final SubscriberService subscriberService;

    public SubscribersRestController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    /**
     * Создает нового абонента.
     * @param subscriberDTO DTO с данными нового абонента.
     * @return созданный {@link Subscriber}.
     */
    @PostMapping("subscriber")
    public Subscriber createSubscriber(@RequestBody SubscriberDTO subscriberDTO){
        return subscriberService.createSubscriber(subscriberDTO);
    }

    /**
     * Пополняет баланс абонента.
     * @param subscriberId идентификатор абонента.
     * @param topUpDTO DTO с суммой пополнения.
     * @return сообщение об успешном пополнении.
     */
    @PatchMapping("subscribers/{subscriberId}/balance")
    public String topUpBalance(@PathVariable Long subscriberId, @RequestBody TopUpDTO topUpDTO){
        subscriberService.addAmountToBalance(subscriberId,topUpDTO.amount());
        return "Successfully topped up the balance";
    }

    /**
     * Устанавливает новый тариф для абонента.
     * @param subscriberId идентификатор абонента.
     * @param tariffId идентификатор нового тарифа.
     * @return сообщение об успешной смене тарифа.
     */
    @PutMapping("/subscribers/{subscriberId}/tariff/{tariffId}")
    public String setTariffForSubscriber(@PathVariable Long subscriberId, @PathVariable Long tariffId){
        subscriberService.setTariffForSubscriber(subscriberId,tariffId);
        return "Successfully set tariff for subscriber.";
    }

    /**
     * Получает полную информацию об абоненте и его тарифе.
     * @param subscriberId идентификатор абонента.
     * @return {@link FullSubscriberAndTariffInfoDTO} с информацией об абоненте и тарифе.
     */
    @GetMapping("subscribers/{subscriberId}")
    public FullSubscriberAndTariffInfoDTO getSubscriberAndTariffInfo(@PathVariable Long subscriberId){
        return subscriberService.getSubscriberAndTariffInfo(subscriberId);
    }
}
