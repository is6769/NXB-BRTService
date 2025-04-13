package org.example.brtservice.services;

import org.example.brtservice.entities.Cdr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrtConsumerService {

    private static final Logger log = LoggerFactory.getLogger(BrtConsumerService.class);
    private final SubscriberService subscriberRepository;

    public BrtConsumerService(SubscriberService subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }


    @RabbitListener(queues = "cdr.queue")
    public void consumeCdr(List<Cdr> cdrs){
        log.info(cdrs.toString());
        log.info("CONSUMED");
    }

    public void saveOurSubscribersCdr(List<Cdr> cdrs){
        List<Cdr> ourCdrs = new ArrayList<>();
        //cdrs.forEach(cdr -> );
    }
}
