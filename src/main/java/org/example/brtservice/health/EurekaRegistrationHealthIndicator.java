package org.example.brtservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Индикатор состояния для проверки регистрации сервиса в Eureka.
 * Реализует {@link HealthIndicator} для интеграции с Spring Boot Actuator.
 */
@Component
public class EurekaRegistrationHealthIndicator implements HealthIndicator {

    /**
     * Имя текущего сервиса, используемое для поиска в Eureka.
     */
    @Value("${spring.application.name}")
    private String serviceName;

    private final EurekaDiscoveryClient eurekaDiscoveryClient;

    public EurekaRegistrationHealthIndicator(EurekaDiscoveryClient eurekaDiscoveryClient) {
        this.eurekaDiscoveryClient = eurekaDiscoveryClient;
    }

    /**
     * Проверяет состояние регистрации сервиса в Eureka.
     * @return {@link Health} объект, указывающий на состояние (UP или DOWN) и детали.
     */
    @Override
    public Health health() {
        try {
            List<ServiceInstance> instances =eurekaDiscoveryClient.getInstances(serviceName);
            boolean isRegistered = !instances.isEmpty();
            return (isRegistered)
                    ? Health.up().withDetail("message", "Service is registered in eureka.").build()
                    : Health.down().withDetail("message", "Service is not registered in eureka.").build();
        } catch (Exception e){
            return Health.down(e).build();
        }
    }
}
