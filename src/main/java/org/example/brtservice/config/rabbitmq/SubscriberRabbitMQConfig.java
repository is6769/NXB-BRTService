package org.example.brtservice.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ для обмена сообщениями, связанными с событиями абонентов.
 * Определяет очереди, обменники и привязки для обработки событий создания абонентов.
 */
@Configuration
public class SubscriberRabbitMQConfig {

    /**
     * Имя очереди для сообщений о создании абонентов.
     */
    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_QUEUE_NAME}")
    private String SUBSCRIBER_CREATED_QUEUE_NAME;

    /**
     * Имя обменника для сообщений о создании абонентов.
     */
    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_EXCHANGE_NAME}")
    private String SUBSCRIBER_CREATED_EXCHANGE_NAME;

    /**
     * Ключ маршрутизации для сообщений о создании абонентов.
     */
    @Value("${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_ROUTING_KEY}")
    private String SUBSCRIBER_CREATED_ROUTING_KEY;

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

    /**
     * Постфикс для имени Dead Letter Queue.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_QUEUE_POSTFIX}")
    private String DEAD_LETTER_QUEUE_POSTFIX;


    /**
     * Создает Dead Letter Exchange для обменника событий создания абонентов.
     * @return {@link DirectExchange} для недоставленных сообщений о создании абонентов.
     */
    @Bean
    public DirectExchange deadLetterSubscriberCreatedExchange(){
        return new DirectExchange(SUBSCRIBER_CREATED_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    /**
     * Создает Dead Letter Queue для очереди событий создания абонентов.
     * @return {@link Queue} для недоставленных сообщений о создании абонентов.
     */
    @Bean
    public Queue deadLetterSubscriberCreatedQueue(){
        return new Queue(SUBSCRIBER_CREATED_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    /**
     * Создает привязку между Dead Letter Exchange и Dead Letter Queue для событий создания абонентов.
     * @return {@link Binding} для маршрутизации недоставленных сообщений.
     */
    @Bean
    public Binding deadLetterSubscriberCreatedBinding(){
        return BindingBuilder
                .bind(deadLetterSubscriberCreatedQueue())
                .to(deadLetterSubscriberCreatedExchange())
                .with(SUBSCRIBER_CREATED_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    /**
     * Создает основной обменник для сообщений о создании абонентов.
     * @return {@link DirectExchange} для сообщений о создании абонентов.
     */
    @Bean
    public DirectExchange subscriberCreatedExchange(){
        return new DirectExchange(SUBSCRIBER_CREATED_EXCHANGE_NAME,false,false);
    }

    /**
     * Создает основную очередь для сообщений о создании абонентов.
     * @return {@link Queue} для сообщений о создании абонентов.
     */
    @Bean
    public Queue subscriberCreatedQueue(){
        return new Queue(SUBSCRIBER_CREATED_QUEUE_NAME);
    }

    /**
     * Создает привязку между основным обменником и очередью для событий создания абонентов с тарифом.
     * @return {@link Binding} для маршрутизации сообщений о создании абонентов.
     */
    @Bean
    public Binding subscriberWithTariffCreatedBinding(){
        return BindingBuilder
                .bind(subscriberCreatedQueue())
                .to(subscriberCreatedExchange())
                .with(SUBSCRIBER_CREATED_ROUTING_KEY);
    }
}
