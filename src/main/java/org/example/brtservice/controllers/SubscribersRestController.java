package org.example.brtservice.controllers;

import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.services.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class SubscribersRestController {

    private final SubscriberService subscriberService;

    public SubscribersRestController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("subscriber")
    public String createSubscriber(SubscriberDTO subscriberDTO){
        subscriberService.createSubscriber(subscriberDTO);
        return "Successfully created subscriber";
    }
}
