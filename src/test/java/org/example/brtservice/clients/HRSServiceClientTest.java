package org.example.brtservice.clients;

import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для класса {@link HRSServiceClient}.
 * Проверяют корректность формирования запросов к HRS сервису.
 */
@ExtendWith(MockitoExtension.class)
class HRSServiceClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @InjectMocks
    private HRSServiceClient hrsServiceClient;

    private final String baseUrl = "http://HRS-service";
    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
    private final Long subscriberId = 1L;
    private final Long tariffId = 2L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hrsServiceClient, "BASE_URL", baseUrl);
    }

    @Test
    void getSystemDatetime_shouldMakeCorrectRequest() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        
        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        
        when(requestHeadersUriSpec.uri(eq(baseUrl), uriCaptor.capture())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(LocalDateTime.class)).thenReturn(testDateTime);

        LocalDateTime result = hrsServiceClient.getSystemDatetime();

        assertEquals(testDateTime, result);
        
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        URI actualUri = uriCaptor.getValue().apply(uriBuilder);
        assertEquals(baseUrl + "/systemDatetime", actualUri.toString());
    }

    @Test
    void getTariffInfo_shouldMakeCorrectRequest() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        TariffDTO expectedTariffDTO = new TariffDTO(tariffId, "Test Tariff", "Description", "30 days", true, null);
        
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        
        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        
        when(requestHeadersUriSpec.uri(eq(baseUrl), uriCaptor.capture())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(TariffDTO.class)).thenReturn(expectedTariffDTO);

        TariffDTO result = hrsServiceClient.getTariffInfo(tariffId);

        assertEquals(expectedTariffDTO, result);
        assertEquals(tariffId, result.id());
        
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        URI actualUri = uriCaptor.getValue().apply(uriBuilder);
        assertEquals(baseUrl + "/tariffs/2", actualUri.toString());
    }

    @Test
    void getTariffInfoBySubscriberId_shouldMakeCorrectRequest() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        TariffDTO expectedTariffDTO = new TariffDTO(tariffId, "Test Tariff", "Description", "30 days", true, null);
        
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        
        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        
        when(requestHeadersUriSpec.uri(eq(baseUrl), uriCaptor.capture())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(TariffDTO.class)).thenReturn(expectedTariffDTO);

        TariffDTO result = hrsServiceClient.getTariffInfoBySubscriberId(subscriberId);

        assertNotNull(result);
        assertEquals(expectedTariffDTO, result);
        
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        URI actualUri = uriCaptor.getValue().apply(uriBuilder);
        assertEquals(baseUrl + "/subscribers/1/tariff", actualUri.toString());
    }

    @Test
    void setTariffForSubscriber_shouldMakeCorrectRequest() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        String expectedResponse = "Successfully set tariff";
        
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        
        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        
        when(requestBodyUriSpec.uri(eq(baseUrl), uriCaptor.capture())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(expectedResponse);

        String result = hrsServiceClient.setTariffForSubscriber(subscriberId, tariffId, testDateTime);

        assertEquals(expectedResponse, result);
        
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        URI actualUri = uriCaptor.getValue().apply(uriBuilder);
        assertEquals(baseUrl + "/subscribers/1/tariff/2?systemDatetime=2023-01-01T12:00", actualUri.toString());
    }
}
