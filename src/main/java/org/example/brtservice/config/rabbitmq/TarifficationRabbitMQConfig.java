package org.example.brtservice.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ для обмена сообщениями, связанными с тарификацией.
 * Определяет очереди, обменники и привязки для обработки данных об использовании звонков.
 */
@Configuration
public class TarifficationRabbitMQConfig {

    /**
     * Имя очереди для сообщений об использовании звонков.
     */
    @Value("${const.rabbitmq.tariffication.CALL_USAGE_QUEUE_NAME}")
    private String CALL_USAGE_QUEUE_NAME;

    /**
     * Имя обменника для сообщений тарификации.
     */
    @Value("${const.rabbitmq.tariffication.TARIFFICATION_EXCHANGE_NAME}")
    private String TARIFFICATION_EXCHANGE_NAME;

    /**
     * Ключ маршрутизации для сообщений об использовании звонков.
     */
    @Value("${const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY}")
    private String CALL_USAGE_ROUTING_KEY;

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
     * Создает Dead Letter Exchange для обменника тарификации.
     * @return {@link TopicExchange} для недоставленных сообщений тарификации.
     */
    @Bean
    public TopicExchange deadLetterTarifficationExchange(){
        return new TopicExchange(TARIFFICATION_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    /**
     * Создает Dead Letter Queue для очереди использования звонков.
     * @return {@link Queue} для недоставленных сообщений об использовании звонков.
     */
    @Bean
    public Queue deadLetterCallUsageQueue(){
        return new Queue(CALL_USAGE_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    /**
     * Создает привязку между Dead Letter Exchange тарификации и Dead Letter Queue использования звонков.
     * @return {@link Binding} для маршрутизации недоставленных сообщений.
     */
    @Bean
    public Binding deadLetterTarifficationBinding(){
        return BindingBuilder
                .bind(deadLetterCallUsageQueue())
                .to(deadLetterTarifficationExchange())
                .with(CALL_USAGE_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    /**
     * Создает основную очередь для сообщений об использовании звонков.
     * @return {@link Queue} для сообщений об использовании звонков.
     */
    @Bean
    public Queue callUsageQueue(){
        return new Queue(CALL_USAGE_QUEUE_NAME);
    }

    /**
     * Создает основной обменник для сообщений тарификации.
     * @return {@link TopicExchange} для сообщений тарификации.
     */
    @Bean
    public TopicExchange tarifficationExchange(){
        return new TopicExchange(TARIFFICATION_EXCHANGE_NAME,false,false);
    }

    /**
     * Создает привязку между основным обменником тарификации и очередью использования звонков.
     * @return {@link Binding} для маршрутизации сообщений об использовании звонков.
     */
    @Bean
    public Binding callUsageBinding(){
        return BindingBuilder
                .bind(callUsageQueue())
                .to(tarifficationExchange())
                .with(CALL_USAGE_ROUTING_KEY);
    }
}
