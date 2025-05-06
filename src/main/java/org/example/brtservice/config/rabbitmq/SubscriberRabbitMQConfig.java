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

    @Bean
    public DirectExchange subscriberCreatedExchange(){
        return new DirectExchange(SUBSCRIBER_CREATED_EXCHANGE_NAME,false,false);
    }

//    @Bean
//    public Queue subscriberWithTariffCreatedQueue(){
//        return new Queue(SUBSCRIBER_WITH_TARIFF_CREATED_QUEUE_NAME);
//    }

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

//    @Bean
//    public Binding subscriberCreatedBinding(){
//        return BindingBuilder
//                .bind(subscriberWithTariffCreatedQueue())
//                .to(subscriberCreatedExchange())
//                .with(SUBSCRIBER_WITH_TARIFF_CREATED_ROUTING_KEY);
//    }
}
