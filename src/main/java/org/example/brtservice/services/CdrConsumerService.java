package org.example.brtservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.exceptions.handlers.RabbitExceptionsHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис-слушатель для обработки CDR (Call Detail Record) из RabbitMQ.
 * Фильтрует CDR, обрабатывая только те, что относятся к абонентам "Ромашки",
 * преобразует их и отправляет для дальнейшей тарификации.
 */
@Service
@Slf4j
public class CdrConsumerService {

    /**
     * Ключ маршрутизации RabbitMQ для отправки данных об использовании звонков на тарификацию.
     */
    @Value("${const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY}")
    private String CALL_USAGE_ROUTING_KEY;

    /**
     * Имя обменника RabbitMQ для отправки данных на тарификацию.
     */
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


    /**
     * Слушает очередь CDR из RabbitMQ.
     * При получении списка CDR, логирует их и передает на обработку.
     * Использует кастомный обработчик ошибок {@link RabbitExceptionsHandler}.
     *
     * @param cdrs список {@link Cdr}, полученных из очереди.
     */
    @RabbitListener(queues = "${const.rabbitmq.cdr.CDR_QUEUE_NAME}", errorHandler = "rabbitExceptionsHandler")
    public void consumeCdr(List<Cdr> cdrs){
        log.info(cdrs.toString());
        processOurSubscribersCdr(cdrs);
    }

    /**
     * Обрабатывает список CDR, фильтруя записи, относящиеся к абонентам "Ромашки".
     * Для каждой такой записи преобразует CDR в DTO, отправляет в очередь тарификации
     * и сохраняет CDR в базе данных.
     *
     * @param cdrs список {@link Cdr} для обработки.
     */
    private void processOurSubscribersCdr(List<Cdr> cdrs){
        cdrs.forEach(cdr -> {
            if (isOur(cdr)){
                CallWithDefaultMetadataDTO callWithDefaultMetadataDTO = cdrService.convertToCallWithDefaultMetadataDTO(cdr);
                rabbitTemplate.convertAndSend(TARIFFICATION_EXCHANGE_NAME,CALL_USAGE_ROUTING_KEY, callWithDefaultMetadataDTO);
                cdrService.save(cdr);
            }
        });
    }

    /**
     * Проверяет, относится ли CDR к абоненту "Ромашки" (т.е. обслуживаемый MSISDN принадлежит "Ромашке").
     * @param cdr объект {@link Cdr} для проверки.
     * @return true, если CDR относится к абоненту "Ромашки", иначе false.
     */
    private boolean isOur(Cdr cdr) {
        return subscriberService.isSubscriberPresent(cdr.getServicedMsisdn());
    }
}
