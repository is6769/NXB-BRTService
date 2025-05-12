package org.example.brtservice.services;

import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.embedded.DefaultCallMetadata;
import org.example.brtservice.entities.Cdr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link CdrConsumerService}.
 * Проверяют логику обработки CDR: фильтрацию по принадлежности абонента "Ромашке",
 * преобразование, отправку в RabbitMQ и сохранение.
 */
@ExtendWith(MockitoExtension.class)
class CdrConsumerServiceTest {

    @Mock
    private SubscriberService subscriberService;

    @Mock
    private CdrService cdrService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CdrConsumerService cdrConsumerService;

    private final String exchangeName = "test-exchange";
    private final String routingKey = "test-routing-key";
    private List<Cdr> testCdrs;
    private Cdr ourSubscriberCdr;
    private Cdr otherSubscriberCdr;
    private CallWithDefaultMetadataDTO metadataDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cdrConsumerService, "TARIFFICATION_EXCHANGE_NAME", exchangeName);
        ReflectionTestUtils.setField(cdrConsumerService, "CALL_USAGE_ROUTING_KEY", routingKey);

        ourSubscriberCdr = new Cdr();
        ourSubscriberCdr.setId(1L);
        ourSubscriberCdr.setServicedMsisdn("79001234567");
        ourSubscriberCdr.setOtherMsisdn("79009876543");
        ourSubscriberCdr.setCallType("01");
        ourSubscriberCdr.setStartDateTime(LocalDateTime.now());
        ourSubscriberCdr.setFinishDateTime(LocalDateTime.now().plusMinutes(5));

        otherSubscriberCdr = new Cdr();
        otherSubscriberCdr.setId(2L);
        otherSubscriberCdr.setServicedMsisdn("71112223344");
        otherSubscriberCdr.setOtherMsisdn("79001234567");
        otherSubscriberCdr.setCallType("02");
        otherSubscriberCdr.setStartDateTime(LocalDateTime.now());
        otherSubscriberCdr.setFinishDateTime(LocalDateTime.now().plusMinutes(3));
        
        testCdrs = Arrays.asList(ourSubscriberCdr, otherSubscriberCdr);
        
        DefaultCallMetadata metadata = new DefaultCallMetadata(
                1L, "01", "79001234567", "79009876543", 
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), 5, "Other");
        
        metadataDTO = new CallWithDefaultMetadataDTO(1L, metadata);
    }

    @Test
    void consumeCdr_shouldProcessOnlyOurSubscribers() {
        when(subscriberService.isSubscriberPresent("79001234567")).thenReturn(true);
        when(subscriberService.isSubscriberPresent("71112223344")).thenReturn(false);
        when(cdrService.convertToCallWithDefaultMetadataDTO(ourSubscriberCdr)).thenReturn(metadataDTO);
        
        cdrConsumerService.consumeCdr(testCdrs);
        
        verify(cdrService).convertToCallWithDefaultMetadataDTO(ourSubscriberCdr);
        verify(cdrService, never()).convertToCallWithDefaultMetadataDTO(otherSubscriberCdr);
        verify(rabbitTemplate).convertAndSend(exchangeName, routingKey, metadataDTO);
        verify(cdrService).save(ourSubscriberCdr);
        verify(cdrService, never()).save(otherSubscriberCdr);
    }

    @Test
    void consumeCdr_withEmptyList_shouldNotProcess() {
        cdrConsumerService.consumeCdr(List.of());
        
        verifyNoInteractions(subscriberService);
        verifyNoInteractions(cdrService);
        verifyNoInteractions(rabbitTemplate);
    }
}
