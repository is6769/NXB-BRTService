package org.example.brtservice.clients;

import org.springframework.web.client.RestClient;

public class HRSServiceClient {

    private final RestClient restClient;

    private String BASE_URI;

    public HRSServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }
}
