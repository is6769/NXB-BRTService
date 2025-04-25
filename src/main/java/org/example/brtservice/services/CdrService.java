package org.example.brtservice.services;

import org.example.brtservice.dtos.CallWithDefaultMetadataDTO;
import org.example.brtservice.embedded.DefaultCallMetadata;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.entities.Subscriber;
import org.example.brtservice.repositories.CdrRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CdrService {

    private final CdrRepository cdrRepository;
    private final SubscriberService subscriberService;

    public CdrService(CdrRepository cdrRepository, SubscriberService subscriberService) {
        this.cdrRepository = cdrRepository;
        this.subscriberService = subscriberService;
    }

    public void save(Cdr cdr){
        cdrRepository.save(cdr);
    }

    public CallWithDefaultMetadataDTO convertToCallWithDefaultMetadataDTO(Cdr cdr) {

        Subscriber subscriber = subscriberService.findSubscriberByMsisdn(cdr.getServicedMsisdn()).orElseThrow(RuntimeException::new);
        boolean isOtherOur = subscriberService.isSubscriberPresent(cdr.getOtherMsisdn());

        DefaultCallMetadata defaultCallMetadata = new DefaultCallMetadata(
                cdr.getId(),
                cdr.getCallType(),
                cdr.getServicedMsisdn(),
                cdr.getOtherMsisdn(),
                cdr.getStartDateTime(),
                cdr.getFinishDateTime(),
                calculateDurationInMinutes(cdr.getStartDateTime(),cdr.getFinishDateTime()),
                (isOtherOur) ? "Ромашка" : "Other"
        );

        return new CallWithDefaultMetadataDTO(subscriber.getId(), defaultCallMetadata);
    }

    private Integer calculateDurationInMinutes(LocalDateTime start, LocalDateTime finish){
        return (int) Math.ceil(Duration.between(finish, start).toSeconds());
    }
}
