package com.yatraflow.admin.dto.response;

import com.yatraflow.admin.enums.LocationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LocationResponse(

        Long id,
        String name,
        String displayName,
        LocationType type,
        String state,
        String country,
        String countryCode,
        BigDecimal latitude,
        BigDecimal longitude,
        String timezone,
        Long parentLocationId,
        String parentLocationName,
        List<LocationIdentifierResponse> identifiers,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
