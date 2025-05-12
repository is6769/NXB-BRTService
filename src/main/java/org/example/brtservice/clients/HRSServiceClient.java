package org.example.brtservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

/**
 * Клиент для взаимодействия с HRS сервисом.
 * Предоставляет методы для установки тарифа, получения системного времени и информации о тарифах.
 */
@Slf4j
@Component
public class HRSServiceClient {

    private final RestClient.Builder restClientBuilder;

    /**
     * Базовый URL для HRS сервиса.
     */
    @Value("${const.hrs-service.BASE_URL}")
    private String BASE_URL;

    public HRSServiceClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    /**
     * Устанавливает тариф для абонента в HRS сервисе.
     * @param subscriberId идентификатор абонента.
     * @param tariffId идентификатор тарифа.
     * @param systemDatetime системное время для операции.
     * @return строка с результатом операции от HRS сервиса.
     */
    public String setTariffForSubscriber(Long subscriberId, Long tariffId, LocalDateTime systemDatetime){
        return restClientBuilder
                .build()
                .put()
                .uri(BASE_URL,uriBuilder -> uriBuilder
                        .path("/subscribers/{subscriberId}/tariff/{tariffId}")
                        .queryParam("systemDatetime",systemDatetime)
                        .build(subscriberId,tariffId))
                .retrieve()
                .body(String.class);
    }

    /**
     * Получает текущее системное время от HRS сервиса.
     * @return {@link LocalDateTime} системное время.
     */
    public LocalDateTime getSystemDatetime() {
        return restClientBuilder
                .build()
                .get()
                .uri(BASE_URL,uriBuilder -> uriBuilder
                        .path("/systemDatetime")
                        .build())
                .retrieve()
                .body(LocalDateTime.class);
    }

    /**
     * Получает информацию о тарифе абонента из HRS сервиса.
     * @param subscriberId идентификатор абонента.
     * @return {@link TariffDTO} с информацией о тарифе.
     */
    public TariffDTO getTariffInfoBySubscriberId(Long subscriberId) {
        return restClientBuilder
                .build()
                .get()
                .uri(BASE_URL, uriBuilder -> uriBuilder
                        .path("/subscribers/{subscriberId}/tariff")
                        .build(subscriberId))
                .retrieve()
                .body(TariffDTO.class);
    }

    /**
     * Получает информацию о конкретном тарифе из HRS сервиса.
     * @param tariffId идентификатор тарифа.
     * @return {@link TariffDTO} с информацией о тарифе.
     */
    public TariffDTO getTariffInfo(Long tariffId) {
        return restClientBuilder
                .build()
                .get()
                .uri(BASE_URL, uriBuilder -> uriBuilder
                        .path("/tariffs/{tariffId}")
                        .build(tariffId))
                .retrieve()
                .body(TariffDTO.class);
    }
}
