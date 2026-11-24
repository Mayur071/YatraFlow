package com.yatraflow.admin.location.service;

import com.yatraflow.admin.location.dto.request.CreateLocationRequest;
import com.yatraflow.admin.location.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.location.dto.response.LocationResponse;
import com.yatraflow.admin.location.enums.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {

    LocationResponse createLocation(CreateLocationRequest request);

    LocationResponse updateLocation(Long locationId, UpdateLocationRequest request);

    LocationResponse getLocationById(Long locationId);

    Page<LocationResponse> searchLocations(
            String search,
            LocationType type,
            String country,
            String state,
            Boolean active,
            Pageable pageable
    );

    LocationResponse activateLocation(Long locationId);

    LocationResponse deactivateLocation(Long locationId);
}
