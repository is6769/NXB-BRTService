package org.example.brtservice.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriberRabbitMQConfig {

    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_QUEUE_NAME}")
    private String SUBSCRIBER_CREATED_QUEUE_NAME;

    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_EXCHANGE_NAME}")
    private String SUBSCRIBER_CREATED_EXCHANGE_NAME;

    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_ROUTING_KEY}")
    private String SUBSCRIBER_CREATED_ROUTING_KEY;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX}")
    private String DEAD_LETTER_EXCHANGE_POSTFIX;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX}")
    private String DEAD_LETTER_ROUTING_KEY_POSTFIX;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_QUEUE_POSTFIX}")
    private String DEAD_LETTER_QUEUE_POSTFIX;


    @Bean
    public DirectExchange deadLetterSubscriberCreatedExchange(){
        return new DirectExchange(SUBSCRIBER_CREATED_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    @Bean
    public Queue deadLetterSubscriberCreatedQueue(){
        return new Queue(SUBSCRIBER_CREATED_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    @Bean
    public Binding deadLetterSubscriberCreatedBinding(){
        return BindingBuilder
                .bind(deadLetterSubscriberCreatedQueue())
                .to(deadLetterSubscriberCreatedExchange())
                .with(SUBSCRIBER_CREATED_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    @Bean
    public DirectExchange subscriberCreatedExchange(){
        return new DirectExchange(SUBSCRIBER_CREATED_EXCHANGE_NAME,false,false);
    }

    @Bean
    public Queue subscriberCreatedQueue(){
        return new Queue(SUBSCRIBER_CREATED_QUEUE_NAME);
    }

    @Bean
    public Binding subscriberWithTariffCreatedBinding(){
        return BindingBuilder
                .bind(subscriberCreatedQueue())
                .to(subscriberCreatedExchange())
                .with(SUBSCRIBER_CREATED_ROUTING_KEY);
    }
}
