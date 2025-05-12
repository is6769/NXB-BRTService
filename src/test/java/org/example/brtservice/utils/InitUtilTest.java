package org.example.brtservice.utils;

import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.services.SubscriberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link InitUtil}.
 * Проверяют корректность инициализации начальных данных абонентов.
 */
@ExtendWith(MockitoExtension.class)
class InitUtilTest {

    @Mock
    private SubscriberService subscriberService;

    @InjectMocks
    private InitUtil initUtil;

    @Captor
    private ArgumentCaptor<SubscriberDTO> subscriberDTOCaptor;

    @Test
    void run_withNoExistingSubscribers_shouldCreateInitialSubscribers() {
        when(subscriberService.findAll()).thenReturn(Collections.emptyList());
        
        initUtil.run();
        
        verify(subscriberService, times(13)).createSubscriber(subscriberDTOCaptor.capture());
        
        List<SubscriberDTO> capturedDTOs = subscriberDTOCaptor.getAllValues();
        assertNotNull(capturedDTOs);
        assertEquals(13, capturedDTOs.size());
        
        SubscriberDTO firstSubscriber = capturedDTOs.get(0);
        assertEquals("79000000001", firstSubscriber.msisdn());
        assertEquals("Иван", firstSubscriber.firstName());
        assertEquals("Иванович", firstSubscriber.secondName());
        assertEquals("Петров", firstSubscriber.surname());
    }

    @Test
    void run_withExistingSubscribers_shouldNotCreateNewSubscribers() {
        Subscriber existingSubscriber = new Subscriber();
        when(subscriberService.findAll()).thenReturn(List.of(existingSubscriber));
        
        initUtil.run();
        
        verify(subscriberService, never()).createSubscriber(any(SubscriberDTO.class));
    }
}
