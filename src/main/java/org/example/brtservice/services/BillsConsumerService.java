package org.example.brtservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.TarifficationBillDTO;
import org.example.brtservice.exceptions.handlers.RabbitExceptionsHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Сервис-слушатель для обработки счетов за тарификацию из RabbitMQ.
 * Списывает сумму счета с баланса соответствующего абонента.
 */
@Slf4j
@Service
public class BillsConsumerService {

    private final SubscriberService subscriberService;

    /**
     * Конструктор для {@link BillsConsumerService}.
     * @param subscriberService сервис для работы с абонентами.
     */
    public BillsConsumerService(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    /**
     * Слушает очередь счетов из RabbitMQ.
     * При получении счета, логирует его и вызывает метод списания суммы с баланса абонента.
     * Использует кастомный обработчик ошибок {@link RabbitExceptionsHandler}.
     *
     * @param bill {@link TarifficationBillDTO} с информацией о счете.
     */
    @RabbitListener(queues = "${const.rabbitmq.bills.BILLS_QUEUE_NAME}", errorHandler = "rabbitExceptionsHandler")
    public void consumeBill(TarifficationBillDTO bill){
        log.info(bill.toString());
        subscriberService.subtractAmountFromBalance(bill.subscriberId(), bill.amount());
    }
}
