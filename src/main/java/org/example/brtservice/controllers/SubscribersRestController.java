package org.example.brtservice.controllers;

import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.dtos.TopUpDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.FullSubscriberAndTariffInfoDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.services.SubscriberService;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubscribersRestController {

    private final SubscriberService subscriberService;

    public SubscribersRestController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("subscriber")
    public Subscriber createSubscriber(@RequestBody SubscriberDTO subscriberDTO){
        return subscriberService.createSubscriber(subscriberDTO);
    }

    @PatchMapping("subscribers/{subscriberId}/balance")
    public String topUpBalance(@PathVariable Long subscriberId, @RequestBody TopUpDTO topUpDTO){
        subscriberService.addAmountToBalance(subscriberId,topUpDTO.amount());
        return "Successfully topped up the balance";
    }

    @PutMapping("/subscribers/{subscriberId}/tariff/{tariffId}")
    public String setTariffForSubscriber(@PathVariable Long subscriberId, @PathVariable Long tariffId){
        subscriberService.setTariffForSubscriber(subscriberId,tariffId);
        return "Successfully set tariff for subscriber.";
    }

    @GetMapping("subscribers/{subscriberId}")
    public FullSubscriberAndTariffInfoDTO getSubscriberAndTariffInfo(@PathVariable Long subscriberId){
        return subscriberService.getSubscriberAndTariffInfo(subscriberId);
    }
}
