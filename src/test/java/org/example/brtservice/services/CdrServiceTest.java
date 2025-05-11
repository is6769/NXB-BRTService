package org.example.brtservice.services;

import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.embedded.DefaultCallMetadata;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.repositories.CdrRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CdrServiceTest {

    @Mock
    private CdrRepository cdrRepository;

    @Mock
    private SubscriberService subscriberService;

    @InjectMocks
    private CdrService cdrService;

    private Cdr cdr;
    private Subscriber servicedSubscriber;

    @BeforeEach
    void setUp() {
        servicedSubscriber = new Subscriber();
        servicedSubscriber.setId(1L);
        servicedSubscriber.setMsisdn("79001112233");

        cdr = new Cdr();
        cdr.setId(100L);
        cdr.setServicedMsisdn("79001112233");
        cdr.setOtherMsisdn("79004445566");
        cdr.setCallType("01");
        cdr.setStartDateTime(LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        cdr.setFinishDateTime(LocalDateTime.of(2024, 1, 1, 12, 5, 30));
    }

    @Test
    void convertToCallWithDefaultMetadataDTO_otherIsOurSubscriber() {
        when(subscriberService.findSubscriberByMsisdn("79001112233")).thenReturn(Optional.of(servicedSubscriber));
        when(subscriberService.isSubscriberPresent("79004445566")).thenReturn(true);

        CallWithDefaultMetadataDTO result = cdrService.convertToCallWithDefaultMetadataDTO(cdr);

        assertNotNull(result);
        assertEquals(servicedSubscriber.getId(), result.subscriberId());

        DefaultCallMetadata metadata = result.metadata();
        assertNotNull(metadata);
        assertEquals(cdr.getId(), metadata.getId());
        assertEquals(cdr.getCallType(), metadata.getCallType());
        assertEquals(cdr.getServicedMsisdn(), metadata.getServicedMsisdn());
        assertEquals(cdr.getOtherMsisdn(), metadata.getOtherMsisdn());
        assertEquals(cdr.getStartDateTime(), metadata.getStartDateTime());
        assertEquals(cdr.getFinishDateTime(), metadata.getFinishDateTime());
        assertEquals(6, metadata.getDurationInMinutes());
        assertEquals("Ромашка", metadata.getOtherOperator());
    }

    @Test
    void convertToCallWithDefaultMetadataDTO_otherIsNotOurSubscriber() {
        when(subscriberService.findSubscriberByMsisdn("79001112233")).thenReturn(Optional.of(servicedSubscriber));
        when(subscriberService.isSubscriberPresent("79004445566")).thenReturn(false);

        CallWithDefaultMetadataDTO result = cdrService.convertToCallWithDefaultMetadataDTO(cdr);

        assertNotNull(result);
        assertEquals(servicedSubscriber.getId(), result.subscriberId());
        assertEquals("Other", result.metadata().getOtherOperator());
    }

    @Test
    void convertToCallWithDefaultMetadataDTO_servicedSubscriberNotFound() {
        when(subscriberService.findSubscriberByMsisdn("79001112233")).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberException.class, () -> cdrService.convertToCallWithDefaultMetadataDTO(cdr));
    }

}
