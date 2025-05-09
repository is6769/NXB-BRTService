package org.example.brtservice.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.entities.Cdr;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CdrConsumerService {

    @Value("${const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY}")
    private String CALL_USAGE_ROUTING_KEY;

    @Value("${const.rabbitmq.tariffication.TARIFFICATION_EXCHANGE_NAME}")
    private String TARIFFICATION_EXCHANGE_NAME;

    private final SubscriberService subscriberService;
    private final CdrService cdrService;
    private final RabbitTemplate rabbitTemplate;

    public CdrConsumerService(SubscriberService subscriberService, CdrService cdrService, RabbitTemplate rabbitTemplate) {
        this.subscriberService = subscriberService;
        this.cdrService = cdrService;
        this.rabbitTemplate = rabbitTemplate;
    }


    @RabbitListener(queues = "${const.rabbitmq.cdr.CDR_QUEUE_NAME}", errorHandler = "rabbitExceptionsHandler")
    public void consumeCdr(List<Cdr> cdrs){
        log.info(cdrs.toString());
        processOurSubscribersCdr(cdrs);
    }

    private void processOurSubscribersCdr(List<Cdr> cdrs){
        cdrs.forEach(cdr -> {
            if (isOur(cdr)){
                CallWithDefaultMetadataDTO callWithDefaultMetadataDTO = cdrService.convertToCallWithDefaultMetadataDTO(cdr);
                rabbitTemplate.convertAndSend(TARIFFICATION_EXCHANGE_NAME,CALL_USAGE_ROUTING_KEY, callWithDefaultMetadataDTO);
                cdrService.save(cdr);
            }
        });
    }

    private boolean isOur(Cdr cdr) {
        return subscriberService.isSubscriberPresent(cdr.getServicedMsisdn());
    }
}
