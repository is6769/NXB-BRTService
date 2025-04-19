package org.example.brtservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.repositories.CdrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BrtConsumerService {


    private final SubscriberService subscriberRepository;


    public BrtConsumerService(SubscriberService subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }


    @RabbitListener(queues = "cdr.queue")
    public void consumeCdr(List<Cdr> cdrs){
        processOurSubscribersCdr(cdrs);
    }

    private void processOurSubscribersCdr(List<Cdr> cdrs){
        List<Cdr> ourCdrs = new ArrayList<>();
        cdrs.forEach(cdr -> {
            if (isOur(cdr)){

            }
        });
    }

    private boolean isOur(Cdr cdr) {
        if ()
    }
}
