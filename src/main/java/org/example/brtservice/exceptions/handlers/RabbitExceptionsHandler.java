package org.example.brtservice.exceptions.handlers;

import com.rabbitmq.client.Channel;
import org.example.brtservice.utils.DLQMessagePublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RabbitExceptionsHandler implements RabbitListenerErrorHandler {

    private final DLQMessagePublisher dlqMessagePublisher;

        public RabbitExceptionsHandler(DLQMessagePublisher dlqMessagePublisher) {
        this.dlqMessagePublisher = dlqMessagePublisher;
    }

    /**
     * Обрабатывает ошибку, возникшую в слушателе RabbitMQ.
     * Логирует ошибку и публикует исходное сообщение в DLQ.
     *
     * @param amqpMessage исходное сообщение AMQP.
     * @param message Spring сообщение, связанное с ошибкой.
     * @param exception исключение, вызвавшее ошибку.
     * @return всегда возвращает null, так как обработка ошибки делегируется DLQ.
     * @throws Exception если возникает ошибка при публикации в DLQ (хотя это маловероятно).
     */
    @Override
    public Object handleError(Message amqpMessage, Channel channel, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        Throwable cause = exception.getCause();
        if (Objects.nonNull(cause)){
            dlqMessagePublisher.publishToDLQ(amqpMessage,cause);
            return null;
        }
        throw exception;
    }
}
