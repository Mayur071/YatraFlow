package com.yatraflow.admin.mapper;

import com.yatraflow.admin.dto.request.CreateLocationRequest;
import com.yatraflow.admin.dto.request.LocationIdentifierRequest;
import com.yatraflow.admin.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.dto.response.LocationIdentifierResponse;
import com.yatraflow.admin.dto.response.LocationResponse;
import com.yatraflow.admin.entity.Location;
import com.yatraflow.admin.entity.LocationIdentifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationMapper {

    public LocationResponse toResponse(Location location) {

        List<LocationIdentifierResponse> identifiers =
                location.getIdentifiers()
                        .stream()
                        .map(this::toIdentifierResponse)
                        .toList();

        Long parentLocationId = location.getParentLocation() != null
                ? location.getParentLocation().getId()
                : null;

        String parentLocationName = location.getParentLocation() != null
                ? location.getParentLocation().getName()
                : null;

        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getDisplayName(),
                location.getType(),
                location.getState(),
                location.getCountry(),
                location.getCountryCode(),
                location.getLatitude(),
                location.getLongitude(),
                location.getTimezone(),
                parentLocationId,
                parentLocationName,
                identifiers,
                location.isActive(),
                location.getCreatedAt(),
                location.getUpdatedAt()
        );
    }

    public LocationIdentifierResponse toIdentifierResponse(
            LocationIdentifier identifier
    ) {
        return new LocationIdentifierResponse(
                identifier.getId(),
                identifier.getCodeType(),
                identifier.getCode()
        );
    }

    public LocationIdentifier toIdentifierEntity(
            LocationIdentifierRequest request
    ) {
        LocationIdentifier identifier = new LocationIdentifier();

        identifier.setCodeType(request.getCodeType());
        identifier.setCode(request.getCode().trim().toUpperCase());

        return identifier;
    }

    public void updateLocationFields(
            Location location,
            CreateLocationRequest request
    ) {
        location.setName(request.getName().trim());
        location.setDisplayName(request.getDisplayName());
        location.setType(request.getType());
        location.setState(request.getState());
        location.setCountry(request.getCountry().trim());
        location.setCountryCode(request.getCountryCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setTimezone(request.getTimezone());
    }

    public void updateLocationFields(
            Location location,
            UpdateLocationRequest request
    ) {
        location.setName(request.getName().trim());
        location.setDisplayName(request.getDisplayName());
        location.setType(request.getType());
        location.setState(request.getState());
        location.setCountry(request.getCountry().trim());
        location.setCountryCode(request.getCountryCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setTimezone(request.getTimezone());
    }
}
