package org.example.brtservice.services;

import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.FullSubscriberAndTariffInfoDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.NoSuchSubscriberException;
import org.example.brtservice.exceptions.SubscriberAlreadyExistsException;
import org.example.brtservice.repositories.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private HRSServiceClient hrsServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private SubscriberService subscriberService;

    private SubscriberDTO subscriberDTO;
    private Subscriber subscriber;
    private LocalDateTime mockSystemTime;

    @BeforeEach
    void setUp() {
        mockSystemTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        subscriberDTO = new SubscriberDTO("79001234567", "John", "Doe", "Smith", 1L, new BigDecimal("100.00"));
        subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("79001234567");
        subscriber.setTariffId(1L);
        subscriber.setBalance(new BigDecimal("100.00"));
        subscriber.setRegisteredAt(mockSystemTime);

        ReflectionTestUtils.setField(subscriberService, "SUBSCRIBER_CREATED_EXCHANGE_NAME", "subscribers.exchange");
        ReflectionTestUtils.setField(subscriberService, "SUBSCRIBER_CREATED_ROUTING_KEY", "subscribers.new");
    }

    @Test
    void createSubscriber_successWithTariff() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberByMsisdn("79001234567")).thenReturn(Optional.empty());
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.of(subscriber));
        when(hrsServiceClient.getTariffInfo(1L)).thenReturn(new TariffDTO(1L, "Test Tariff", "test", "0 days", true,null)); // Assuming TariffDTO structure
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> {
            Subscriber s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Subscriber createdSubscriber = subscriberService.createSubscriber(subscriberDTO);

        assertNotNull(createdSubscriber);
        assertEquals(1L, createdSubscriber.getId());
        assertEquals("79001234567", createdSubscriber.getMsisdn());
        assertEquals(mockSystemTime, createdSubscriber.getRegisteredAt());

        verify(subscriberRepository).save(any(Subscriber.class));
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Map.class));
        verify(hrsServiceClient).setTariffForSubscriber(eq(1L), eq(1L), eq(mockSystemTime));
    }

    @Test
    void createSubscriber_successWithoutTariff() {
        SubscriberDTO noTariffDto = new SubscriberDTO("79001234568", "Jane", "Doe", "Smith", null, new BigDecimal("50.00"));
        Subscriber createdMockSubscriber = new Subscriber(2L,"79001234568", "Jane", "Doe", "Smith", null, new BigDecimal("50.00"),mockSystemTime);
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberByMsisdn("79001234568")).thenReturn(Optional.empty());
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> {
            Subscriber s = invocation.getArgument(0);
            s.setId(2L);
            return s;
        });
        when(subscriberRepository.findSubscriberById(2L)).thenReturn(Optional.of(createdMockSubscriber));


        Subscriber createdSubscriber = subscriberService.createSubscriber(noTariffDto);

        assertNotNull(createdSubscriber);
        assertEquals(2L, createdSubscriber.getId());
        assertEquals("79001234568", createdSubscriber.getMsisdn());

        verify(hrsServiceClient, never()).getTariffInfo(anyLong());
        verify(subscriberRepository).save(any(Subscriber.class));
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Map.class));
        verify(hrsServiceClient, never()).setTariffForSubscriber(anyLong(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void createSubscriber_alreadyExists() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberByMsisdn("79001234567")).thenReturn(Optional.of(subscriber));

        assertThrows(SubscriberAlreadyExistsException.class, () -> subscriberService.createSubscriber(subscriberDTO));

        verify(subscriberRepository, never()).save(any(Subscriber.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
        verify(hrsServiceClient, never()).setTariffForSubscriber(anyLong(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void createSubscriber_hrsTariffInfoCheckFails() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberByMsisdn("79001234567")).thenReturn(Optional.empty());
        when(hrsServiceClient.getTariffInfo(1L)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Tariff not found"));

        assertThrows(HttpClientErrorException.class, () -> subscriberService.createSubscriber(subscriberDTO));

        verify(subscriberRepository, never()).save(any(Subscriber.class));
    }

    @Test
    void setTariffForSubscriber_success() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.of(subscriber));
        when(hrsServiceClient.getTariffInfo(2L)).thenReturn(new TariffDTO(2L, "New Tariff", null, null, true,null));
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(subscriber);

        subscriberService.setTariffForSubscriber(1L, 2L);

        assertEquals(2L, subscriber.getTariffId());
        verify(subscriberRepository).save(subscriber);
        verify(hrsServiceClient).setTariffForSubscriber(1L, 2L, mockSystemTime);
    }

    @Test
    void setTariffForSubscriber_subscriberNotFound() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberException.class, () -> subscriberService.setTariffForSubscriber(1L, 2L));

        verify(subscriberRepository, never()).save(any(Subscriber.class));
        verify(hrsServiceClient, never()).setTariffForSubscriber(anyLong(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void setTariffForSubscriber_hrsTariffInfoCheckFails() {
        when(hrsServiceClient.getSystemDatetime()).thenReturn(mockSystemTime);
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.of(subscriber));
        when(hrsServiceClient.getTariffInfo(2L)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Tariff not found"));

        assertThrows(HttpClientErrorException.class, () -> subscriberService.setTariffForSubscriber(1L, 2L));
        verify(subscriberRepository, never()).save(any(Subscriber.class));
    }


    @Test
    void getSubscriberAndTariffInfo_successWithTariff() {
        TariffDTO mockTariffDTO = new TariffDTO(1L, "Test Tariff", "test", "0 days", true,null);
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.of(subscriber));
        when(hrsServiceClient.getTariffInfoBySubscriberId(1L)).thenReturn(mockTariffDTO);

        FullSubscriberAndTariffInfoDTO result = subscriberService.getSubscriberAndTariffInfo(1L);

        assertNotNull(result);
        assertEquals(subscriber.getId(), result.subscriber().getId());
        assertEquals(mockTariffDTO.id(), result.tariff().id());
    }

    @Test
    void getSubscriberAndTariffInfo_successNoTariffSetOnSubscriber() {
        subscriber.setTariffId(null); // No tariff assigned to subscriber
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.of(subscriber));

        FullSubscriberAndTariffInfoDTO result = subscriberService.getSubscriberAndTariffInfo(1L);

        assertNotNull(result);
        assertEquals(subscriber.getId(), result.subscriber().getId());
        assertNull(result.tariff());
        verify(hrsServiceClient, never()).getTariffInfoBySubscriberId(anyLong());
    }

    @Test
    void getSubscriberAndTariffInfo_subscriberNotFound() {
        when(subscriberRepository.findSubscriberById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberException.class, () -> subscriberService.getSubscriberAndTariffInfo(1L));
    }
    
    @Test
    void findAll_returnsListOfSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(List.of(subscriber));
        List<Subscriber> result = subscriberService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(subscriber.getMsisdn(), result.get(0).getMsisdn());
        verify(subscriberRepository).findAll();
    }
}
