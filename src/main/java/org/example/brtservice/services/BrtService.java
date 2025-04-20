package org.example.brtservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.brtservice.clients.HRSServiceClient;
import org.example.brtservice.dtos.CdrWithMetadataDTO;
import org.example.brtservice.entities.Cdr;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BrtService {


    private final SubscriberService subscriberService;
    private final CdrService cdrService;
    private final HRSServiceClient hrsServiceClient;


    public BrtService(SubscriberService subscriberService, CdrService cdrService, HRSServiceClient hrsServiceClient) {
        this.subscriberService = subscriberService;
        this.cdrService = cdrService;
        this.hrsServiceClient = hrsServiceClient;
    }


    @RabbitListener(queues = "cdr.queue")
    public void consumeCdr(List<Cdr> cdrs){
        processOurSubscribersCdr(cdrs);
    }

    private void processOurSubscribersCdr(List<Cdr> cdrs){
        List<Cdr> ourCdrs = new ArrayList<>();
        cdrs.forEach(cdr -> {
            if (isOur(cdr)){
                cdrService.save(cdr);
                CdrWithMetadataDTO cdrWithMetadataDTO = cdrService.convertToCdrWithMetadataDTO(cdr);
                hrsServiceClient.tarificateCdr(cdrWithMetadataDTO);
            }
        });
    }

    private boolean isOur(Cdr cdr) {
        if (subscriberService.isSubscriberPresent(cdr.getServicedMsisdn())){
            return true;
        }
        return false;
    }
}
