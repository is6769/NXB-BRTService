package org.example.brtservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.TarifficationBillDTO;
import org.example.brtservice.entities.Subscriber;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillsConsumerService {

    private final SubscriberService subscriberService;

    public BillsConsumerService(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @RabbitListener(queues = "${const.rabbitmq.bills.BILLS_QUEUE_NAME}")
    public void consumeBill(TarifficationBillDTO bill){
        log.info(bill.toString());
        subscriberService.subtractAmountFromBalance(bill.subscriberId(),bill.amount());
    }
}
