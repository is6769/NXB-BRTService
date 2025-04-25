package org.example.brtservice.clients;

import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.dtos.TarifficationBillDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
public class HRSServiceClient {

    private final RestClient restClient;

    @Value("${const.hrs-service.BASE_URL}")
    private String BASE_URL;

    public HRSServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TarifficationBillDTO chargeCdr(CallWithDefaultMetadataDTO callWithDefaultMetadataDTO){
        return restClient
                .post()
                .uri(BASE_URL)
                .retrieve()
                .body(TarifficationBillDTO.class);
    }

    public TarifficationBillDTO setTariffForSubscriber(Long subscriberId, Long tariffId, LocalDateTime currentUnrealDateTime){
        return restClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL+"/subscribers/{subscriberId}/tariff/{tariffId}")
                        .queryParam("currentUnrealDateTime",currentUnrealDateTime)
                        .build(subscriberId,tariffId))
                .retrieve()
                .body(TarifficationBillDTO.class);
    }
}
