package org.example.brtservice.clients;

import org.example.brtservice.dtos.CdrWithMetadataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HRSServiceClient {

    private final RestClient restClient;

    @Value("${const.hrs-service.BASE_URI}")
    private String BASE_URI;

    public HRSServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String tarificateCdr(CdrWithMetadataDTO cdrWithMetadataDTO){
        return restClient
                .post()
                .uri(BASE_URI)
                .retrieve()
                .body(String.class);
    }
}
