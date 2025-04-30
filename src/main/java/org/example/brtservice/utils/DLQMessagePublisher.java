package org.example.brtservice.utils;

import org.example.brtservice.clients.HRSServiceClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DLQMessagePublisher {

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX}")
    private String DEAD_LETTER_EXCHANGE_POSTFIX;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX}")
    private String DEAD_LETTER_ROUTING_KEY_POSTFIX;

    private final RabbitTemplate rabbitTemplate;
    private final HRSServiceClient hrsServiceClient;

    public DLQMessagePublisher(RabbitTemplate rabbitTemplate, HRSServiceClient hrsServiceClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.hrsServiceClient = hrsServiceClient;
    }

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
