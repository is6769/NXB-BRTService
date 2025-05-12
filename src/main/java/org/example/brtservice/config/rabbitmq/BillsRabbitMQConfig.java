package org.example.brtservice.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ для обмена сообщениями, связанными со счетами (bills).
 * Определяет очереди, обменники и привязки для обработки информации о счетах.
 */
@Configuration
public class BillsRabbitMQConfig {

    /**
     * Имя очереди для сообщений о счетах.
     */
    @Value("${const.rabbitmq.bills.BILLS_QUEUE_NAME}")
    private String BILLS_QUEUE_NAME;

    /**
     * Имя обменника для сообщений о счетах.
     */
    @Value("${const.rabbitmq.bills.BILLS_EXCHANGE_NAME}")
    private String BILLS_EXCHANGE_NAME;

    /**
     * Ключ маршрутизации для сообщений о счетах.
     */
    @Value("${const.rabbitmq.bills.BILLS_ROUTING_KEY}")
    private String BILLS_ROUTING_KEY;

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
     * Создает Dead Letter Queue для очереди счетов.
     * @return {@link Queue} для недоставленных сообщений о счетах.
     */
    @Bean
    public Queue deadLetterBillsQueue(){
        return new Queue(BILLS_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    /**
     * Создает Dead Letter Exchange для обменника счетов.
     * @return {@link DirectExchange} для недоставленных сообщений о счетах.
     */
    @Bean
    public DirectExchange deadLetterBillsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    /**
     * Создает привязку между Dead Letter Exchange и Dead Letter Queue для сообщений о счетах.
     * @return {@link Binding} для маршрутизации недоставленных сообщений о счетах.
     */
    @Bean
    public Binding deadLetterBillsBinding(){
        return BindingBuilder
                .bind(deadLetterBillsQueue())
                .to(deadLetterBillsExchange())
                .with(BILLS_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    /**
     * Создает основную очередь для сообщений о счетах.
     * @return {@link Queue} для сообщений о счетах.
     */
    @Bean
    public Queue billsQueue(){
        return new Queue(BILLS_QUEUE_NAME);
    }

    /**
     * Создает основной обменник для сообщений о счетах.
     * @return {@link DirectExchange} для сообщений о счетах.
     */
    @Bean
    public DirectExchange billsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME,false,false);
    }

    /**
     * Создает привязку между основным обменником счетов и очередью счетов.
     * @return {@link Binding} для маршрутизации сообщений о счетах.
     */
    @Bean
    public Binding billsBinding(){
        return BindingBuilder
                .bind(billsQueue())
                .to(billsExchange())
                .with(BILLS_ROUTING_KEY);
    }
}
