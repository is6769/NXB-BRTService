package org.example.brtservice.services;

import org.example.brtservice.dtos.TarifficationBillDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

/**
 * Тесты для класса {@link BillsConsumerService}.
 * Проверяют корректность обработки входящих счетов и списания средств с баланса абонента.
 */
@ExtendWith(MockitoExtension.class)
class BillsConsumerServiceTest {

    @Mock
    private SubscriberService subscriberService;

    @InjectMocks
    private BillsConsumerService billsConsumerService;

    @Test
    void consumeBill_shouldSubtractAmountFromSubscriberBalance() {
        Long subscriberId = 1L;
        BigDecimal amount = new BigDecimal("50.00");
        TarifficationBillDTO billDTO = new TarifficationBillDTO(amount, "y.e.", subscriberId);
        
        billsConsumerService.consumeBill(billDTO);
        
        verify(subscriberService).subtractAmountFromBalance(subscriberId, amount);
    }
}
