package org.example.brtservice.dtos;

import org.example.brtservice.entities.Subscriber;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record SubscriberDTO(
    String msisdn,
    String firstName,
    @Nullable String secondName,
    String surname,
    @Nullable Long tariffId,
    @Nullable BigDecimal balance
) {
    public Subscriber toEntity(){
        return Subscriber.builder()
                .msisdn(this.msisdn)
                .firstName(this.firstName)
                .secondName(this.secondName)
                .surname(this.surname)
                .tariffId(this.tariffId)
                .balance(this.balance)
                .build();
    }
}
