package org.example.brtservice.dtos;

import lombok.Builder;
import org.example.brtservice.embedded.DefaultCdrMetadata;

import java.time.LocalDateTime;

@Builder
public record CdrWithMetadataDTO(
        Long id,
        String callType,
        String servicedMsisdn,
        String otherMsisdn,
        LocalDateTime startDateTime,
        LocalDateTime finishDateTime,
        DefaultCdrMetadata cdrMetadata
) {

}
