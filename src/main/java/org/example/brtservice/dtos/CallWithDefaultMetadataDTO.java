package org.example.brtservice.dtos;

import lombok.Builder;
import org.example.brtservice.embedded.DefaultCallMetadata;

@Builder
public record CallWithDefaultMetadataDTO(
        Long subscriberId,
        DefaultCallMetadata metadata

) {

}
