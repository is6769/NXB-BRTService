package org.example.brtservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient restClient(){
        return RestClient.create();
    }
}
