package org.example.brtservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.brtservice.dtos.SubscriberDTO;
import org.example.brtservice.dtos.TopUpDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.FullSubscriberAndTariffInfoDTO;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.exceptions.handlers.RestExceptionsHandler;
import org.example.brtservice.services.SubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для класса {@link SubscribersRestController}.
 * Проверяют корректность работы эндпоинтов для управления абонентами.
 */
@ExtendWith(MockitoExtension.class)
class SubscribersRestControllerTest {

    @Mock
    private SubscriberService subscriberService;

    @InjectMocks
    private SubscribersRestController subscribersRestController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long subscriberId = 1L;
    private final Long tariffId = 2L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscribersRestController)
                .setControllerAdvice(new RestExceptionsHandler(null))
                .build();
    }

    @Test
    void createSubscriber_shouldReturnCreatedSubscriber() throws Exception {
        SubscriberDTO subscriberDTO = new SubscriberDTO("79001234567", "John", "Doe", "Smith", tariffId, new BigDecimal("100.00"));
        
        Subscriber createdSubscriber = new Subscriber();
        createdSubscriber.setId(subscriberId);
        createdSubscriber.setMsisdn("79001234567");
        createdSubscriber.setFirstName("John");
        createdSubscriber.setSecondName("Doe");
        createdSubscriber.setSurname("Smith");
        createdSubscriber.setTariffId(tariffId);
        createdSubscriber.setBalance(new BigDecimal("100.00"));
        createdSubscriber.setRegisteredAt(LocalDateTime.now());
        
        when(subscriberService.createSubscriber(any(SubscriberDTO.class))).thenReturn(createdSubscriber);
        
        mockMvc.perform(post("/subscriber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subscriberId))
                .andExpect(jsonPath("$.msisdn").value("79001234567"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.secondName").value("Doe"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.tariffId").value(tariffId))
                .andExpect(jsonPath("$.balance").value(100.0));
        
        verify(subscriberService).createSubscriber(any(SubscriberDTO.class));
    }

    @Test
    void topUpBalance_shouldReturnSuccessMessage() throws Exception {
        TopUpDTO topUpDTO = new TopUpDTO(new BigDecimal("50.00"),"y.e.");
        
        mockMvc.perform(patch("/subscribers/{subscriberId}/balance", subscriberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topUpDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully topped up the balance"));
        
        verify(subscriberService).addAmountToBalance(subscriberId, topUpDTO.amount());
    }

    @Test
    void setTariffForSubscriber_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(put("/subscribers/{subscriberId}/tariff/{tariffId}", subscriberId, tariffId))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully set tariff for subscriber."));
        
        verify(subscriberService).setTariffForSubscriber(subscriberId, tariffId);
    }

    @Test
    void getSubscriberAndTariffInfo_shouldReturnSubscriberWithTariff() throws Exception {
        FullSubscriberAndTariffInfoDTO mockDto = new FullSubscriberAndTariffInfoDTO(null, null);
        
        when(subscriberService.getSubscriberAndTariffInfo(subscriberId)).thenReturn(mockDto);
        
        mockMvc.perform(get("/subscribers/{subscriberId}", subscriberId))
                .andExpect(status().isOk());
        
        verify(subscriberService).getSubscriberAndTariffInfo(subscriberId);
    }
}
