package org.example.brtservice.services;

import org.example.brtservice.dtos.CdrWithMetadataDTO;
import org.example.brtservice.embedded.DefaultCdrMetadata;
import org.example.brtservice.entities.Cdr;
import org.example.brtservice.repositories.CdrRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

    public CdrWithMetadataDTO convertToCdrWithMetadataDTO(Cdr cdr) {
        CdrWithMetadataDTO cdrWithMetadataDTO = CdrWithMetadataDTO.builder()
                .id(cdr.getId())
                .callType(cdr.getCallType())
                .servicedMsisdn(cdr.getServicedMsisdn())
                .otherMsisdn(cdr.getOtherMsisdn())
                .startDateTime(cdr.getStartDateTime())
                .finishDateTime(cdr.getFinishDateTime())
                .cdrMetadata(
                        new DefaultCdrMetadata(
                                (int) Math.ceil(Duration.between(cdr.getFinishDateTime(), cdr.getStartDateTime()).toSeconds()),
                                null
                        )
                )
                .build();

        boolean isOtherOur = subscriberService.isSubscriberPresent(cdr.getOtherMsisdn());

        if (isOtherOur){
            cdrWithMetadataDTO.cdrMetadata().setOtherOperator("Ромашка");
        }else {
            cdrWithMetadataDTO.cdrMetadata().setOtherOperator("Other");
        }


        return cdrWithMetadataDTO;
    }
}
