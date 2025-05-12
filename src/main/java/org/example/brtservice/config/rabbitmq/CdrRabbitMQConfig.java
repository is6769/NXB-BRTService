package org.example.brtservice.config.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ для обмена сообщениями CDR (Call Detail Record).
 * Определяет очереди, обменники и привязки для обработки CDR файлов.
 */
@Configuration
public class CdrRabbitMQConfig {

    /**
     * Имя очереди для CDR сообщений.
     */
    @Value("${const.rabbitmq.cdr.CDR_QUEUE_NAME}")
    private String CDR_QUEUE_NAME;

    /**
     * Имя обменника для CDR сообщений.
     */
    @Value("${const.rabbitmq.cdr.CDR_EXCHANGE_NAME}")
    private String CDR_EXCHANGE_NAME;

    /**
     * Ключ маршрутизации для CDR сообщений.
     */
    @Value("${const.rabbitmq.cdr.CDR_ROUTING_KEY}")
    private String CDR_ROUTING_KEY;

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
     * Создает Dead Letter Exchange для CDR обменника.
     * @return {@link DirectExchange} для недоставленных CDR сообщений.
     */
    @Bean
    public DirectExchange deadLetterCdrExchange(){
        return new DirectExchange(CDR_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    /**
     * Создает Dead Letter Queue для CDR очереди.
     * @return {@link Queue} для недоставленных CDR сообщений.
     */
    @Bean
    public Queue deadLetterCdrQueue(){
        return new Queue(CDR_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    /**
     * Создает привязку между Dead Letter Exchange и Dead Letter Queue для CDR сообщений.
     * @return {@link Binding} для маршрутизации недоставленных CDR сообщений.
     */
    @Bean
    public Binding deadLetterCdrBinding(){
        return BindingBuilder
                .bind(deadLetterCdrQueue())
                .to(deadLetterCdrExchange())
                .with(CDR_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    /**
     * Создает основную очередь для CDR сообщений.
     * @return {@link Queue} для CDR сообщений.
     */
    @Bean
    public Queue cdrQueue(){
        return new Queue(CDR_QUEUE_NAME);
    }

    /**
     * Создает основной обменник для CDR сообщений.
     * @return {@link DirectExchange} для CDR сообщений.
     */
    @Bean
    public DirectExchange cdrExchange(){
        return new DirectExchange(CDR_EXCHANGE_NAME,false,false);
    }

    /**
     * Создает привязку между основным CDR обменником и CDR очередью.
     * @return {@link Binding} для маршрутизации CDR сообщений.
     */
    @Bean
    public Binding cdrBinding(){
        return BindingBuilder
                .bind(cdrQueue())
                .to(cdrExchange())
                .with(CDR_ROUTING_KEY);
    }
}
