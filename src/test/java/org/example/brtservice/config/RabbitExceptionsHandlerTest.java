package org.example.brtservice.config;

import org.example.brtservice.exceptions.handlers.RabbitExceptionsHandler;
import org.example.brtservice.utils.DLQMessagePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link RabbitExceptionsHandler}.
 * Проверяют корректность обработки ошибок при прослушивании RabbitMQ очередей.
 */
@ExtendWith(MockitoExtension.class)
class RabbitExceptionsHandlerTest {

    @Mock
    private DLQMessagePublisher dlqMessagePublisher;

    @InjectMocks
    private RabbitExceptionsHandler rabbitExceptionsHandler;

    @Test
    void handleError_shouldPublishToDLQ() throws Exception {
        Message message = mock(Message.class);
        RuntimeException cause = new RuntimeException("Test exception");
        ListenerExecutionFailedException exception = new ListenerExecutionFailedException("Listener execution failed", cause, message);
        
        rabbitExceptionsHandler.handleError(message,null,null ,exception );
        
        verify(dlqMessagePublisher).publishToDLQ(message, cause);
    }
}
