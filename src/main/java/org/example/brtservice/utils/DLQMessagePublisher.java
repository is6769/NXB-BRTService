package org.example.brtservice.utils;

import org.example.brtservice.clients.HRSServiceClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Утилитарный класс для публикации сообщений в Dead Letter Queue (DLQ).
 * Добавляет к исходному сообщению информацию об ошибке и временную метку.
 */
@Component
public class DLQMessagePublisher {

    /**
     * Постфикс для имени Dead Letter Exchange.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX}")
    private String DEAD_LETTER_EXCHANGE_POSTFIX;

    /**
     * Постфикс для ключа маршрутизации Dead Letter Queue.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX}")
    private String DEAD_LETTER_ROUTING_KEY_POSTFIX;

    private final RabbitTemplate rabbitTemplate;
    private final HRSServiceClient hrsServiceClient;

    public DLQMessagePublisher(RabbitTemplate rabbitTemplate, HRSServiceClient hrsServiceClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.hrsServiceClient = hrsServiceClient;
    }

    /**
     * Публикует сообщение в DLQ.
     * Исходное сообщение обогащается заголовками с типом исключения, сообщением об ошибке
     * и временной меткой, полученной от HRS сервиса.
     * Новое сообщение отправляется в DLX (Dead Letter Exchange) с DLQ роутинг ключом,
     * которые формируются на основе оригинальных обменника и ключа маршрутизации.
     *
     * @param originalMessage исходное сообщение, вызвавшее ошибку.
     * @param cause исключение, ставшее причиной отправки в DLQ.
     */
    public void publishToDLQ(Message originalMessage, Throwable cause){
        String originalExchange = originalMessage.getMessageProperties().getReceivedExchange();
        String originalRoutingKey = originalMessage.getMessageProperties().getReceivedRoutingKey();
        Message enhancedMessage = MessageBuilder
                .fromMessage(originalMessage)
                .setHeader("x-exception-type", cause.getClass().getName())
                .setHeader("x-error-msg", cause.getMessage())
                .setHeader("x-timestamp", hrsServiceClient.getSystemDatetime())
                .build();

        rabbitTemplate.convertAndSend(originalExchange+DEAD_LETTER_EXCHANGE_POSTFIX,originalRoutingKey+DEAD_LETTER_ROUTING_KEY_POSTFIX,enhancedMessage);
    }
}
