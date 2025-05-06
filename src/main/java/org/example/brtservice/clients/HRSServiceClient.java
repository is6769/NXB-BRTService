package org.example.brtservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.dtos.SubscriberTariffDTO;
import org.example.brtservice.dtos.fullSubscriberAndTariffInfo.TariffDTO;
import org.example.brtservice.dtos.TarifficationBillDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Slf4j
@Component
public class HRSServiceClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${const.hrs-service.BASE_URL}")
    private String BASE_URL;

    public HRSServiceClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public String setTariffForSubscriber(Long subscriberId, Long tariffId, LocalDateTime systemDatetime){
        return restClientBuilder
                .build()
                .put()
                .uri(BASE_URL,uriBuilder -> uriBuilder
                        .path("/subscribers/{subscriberId}/tariff/{tariffId}")
                        .queryParam("currentUnrealDateTime",systemDatetime)
                        .build(subscriberId,tariffId))
                .retrieve()
                .body(String.class);
    }

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

    public TariffDTO getTariffInfo(Long subscriberId) {
        return restClientBuilder
                .build()
                .get()
                .uri(BASE_URL, uriBuilder -> uriBuilder
                        .path("/subscribers/{subscriberId}/tariff")
                        .build(subscriberId))
                .retrieve()
                .body(TariffDTO.class);
    }
}
