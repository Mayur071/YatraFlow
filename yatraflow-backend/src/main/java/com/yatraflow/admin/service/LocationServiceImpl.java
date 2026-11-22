
package com.yatraflow.admin.service.impl;

import com.yatraflow.admin.dto.request.CreateLocationRequest;
import com.yatraflow.admin.dto.request.LocationIdentifierRequest;
import com.yatraflow.admin.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.dto.response.LocationResponse;
import com.yatraflow.admin.entity.Location;
import com.yatraflow.admin.entity.LocationIdentifier;
import com.yatraflow.admin.mapper.LocationMapper;
import com.yatraflow.admin.repository.LocationIdentifierRepository;
import com.yatraflow.admin.repository.LocationRepository;
import com.yatraflow.admin.repository.specification.LocationSpecification;
import com.yatraflow.admin.service.LocationService;
import com.yatraflow.admin.enums.LocationType;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationIdentifierRepository locationIdentifierRepository;
    private final LocationMapper locationMapper;

    // CREATE LOCATION
    @Override
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {

        Location location = new Location();

        locationMapper.updateLocationFields(location, request);

        if (request.getParentLocationId() != null) {

            Location parentLocation = findLocationById(
                    request.getParentLocationId()
            );

            validateParentLocation(parentLocation, null);

            location.setParentLocation(parentLocation);
        }

        if (request.getIdentifiers() != null
                && !request.getIdentifiers().isEmpty()) {

            validateAndAddIdentifiers(
                    location,
                    request.getIdentifiers(),
                    null
            );
        }

        Location savedLocation = locationRepository.save(location);

        return locationMapper.toResponse(savedLocation);
    }

    // UPDATE LOCATION
    @Override
    @Transactional
    public LocationResponse updateLocation(
            Long locationId,
            UpdateLocationRequest request
    ) {

        Location location = findLocationById(locationId);

        locationMapper.updateLocationFields(location, request);

        if (request.getParentLocationId() != null) {

            Location parentLocation = findLocationById(
                    request.getParentLocationId()
            );

            validateParentLocation(parentLocation, locationId);

            location.setParentLocation(parentLocation);

        } else {
            location.setParentLocation(null);
        }

        if (request.getIdentifiers() != null) {

            synchronizeIdentifiers(
                    location,
                    request.getIdentifiers()
            );

        } else {
            location.getIdentifiers().clear();
        }

        Location updatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(updatedLocation);
    }

    // GET LOCATION BY ID
    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long locationId) {

        Location location = findLocationById(locationId);

        return locationMapper.toResponse(location);
    }

    // SEARCH + FILTER + PAGINATION + SORTING
    @Override
    @Transactional(readOnly = true)
    public Page<LocationResponse> searchLocations(
            String search,
            LocationType type,
            String country,
            String state,
            Boolean active,
            Pageable pageable
    ) {

        return locationRepository.findAll(
                LocationSpecification.filters(
                        search,
                        type,
                        country,
                        state,
                        active
                ),
                pageable
        ).map(locationMapper::toResponse);
    }

    // ACTIVATE LOCATION
    @Override
    @Transactional
    public LocationResponse activateLocation(Long locationId) {

        Location location = findLocationById(locationId);

        location.setActive(true);

        Location updatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(updatedLocation);
    }

    // DEACTIVATE LOCATION
    @Override
    @Transactional
    public LocationResponse deactivateLocation(Long locationId) {

        Location location = findLocationById(locationId);

        location.setActive(false);

        Location updatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(updatedLocation);
    }

    // REUSABLE LOCATION LOOKUP
    private Location findLocationById(Long locationId) {

        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with ID: " + locationId
                ));
    }

    // PARENT VALIDATION
    private void validateParentLocation(
            Location parentLocation,
            Long currentLocationId
    ) {

        if (!parentLocation.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive location cannot be used as a parent"
            );
        }

        if (currentLocationId != null
                && parentLocation.getId().equals(currentLocationId)) {

            throw new IllegalArgumentException(
                    "Location cannot be its own parent"
            );
        }

        Location ancestor = parentLocation;

        while (ancestor != null) {

            if (currentLocationId != null
                    && ancestor.getId().equals(currentLocationId)) {

                throw new IllegalArgumentException(
                        "Circular location hierarchy is not allowed"
                );
            }

            ancestor = ancestor.getParentLocation();
        }
    }

    // CREATE IDENTIFIERS WITH DUPLICATE VALIDATION
    private void validateAndAddIdentifiers(
            Location location,
            List<LocationIdentifierRequest> identifiers,
            Long currentLocationId
    ) {

        Set<String> requestedKeys = new HashSet<>();

        for (LocationIdentifierRequest request : identifiers) {

            String normalizedCode = normalizeCode(request.getCode());

            String identifierKey = createIdentifierKey(
                    request.getCodeType().name(),
                    normalizedCode
            );

            if (!requestedKeys.add(identifierKey)) {

                throw new ResourceAlreadyExistsException(
                        "Duplicate identifier in request: " + normalizedCode
                );
            }

            LocationIdentifier existingIdentifier =
                    locationIdentifierRepository.findByCodeAndCodeType(
                            normalizedCode,
                            request.getCodeType()
                    ).orElse(null);

            if (existingIdentifier != null) {

                Long existingLocationId =
                        existingIdentifier.getLocation().getId();

                if (currentLocationId == null
                        || !existingLocationId.equals(currentLocationId)) {

                    throw new ResourceAlreadyExistsException(
                            "Location identifier already exists: "
                                    + normalizedCode
                    );
                }
            }

            LocationIdentifier identifier =
                    locationMapper.toIdentifierEntity(request);

            identifier.setCode(normalizedCode);

            location.addIdentifier(identifier);
        }
    }

    // UPDATE IDENTIFIERS
    private void synchronizeIdentifiers(
            Location location,
            List<LocationIdentifierRequest> requestedIdentifiers
    ) {

        Set<String> requestedKeys = new HashSet<>();

        // Validate duplicates in request and database
        for (LocationIdentifierRequest request : requestedIdentifiers) {

            String normalizedCode = normalizeCode(request.getCode());

            String identifierKey = createIdentifierKey(
                    request.getCodeType().name(),
                    normalizedCode
            );

            if (!requestedKeys.add(identifierKey)) {

                throw new ResourceAlreadyExistsException(
                        "Duplicate identifier in request: " + normalizedCode
                );
            }

            LocationIdentifier existingIdentifier =
                    locationIdentifierRepository.findByCodeAndCodeType(
                            normalizedCode,
                            request.getCodeType()
                    ).orElse(null);

            if (existingIdentifier != null
                    && !existingIdentifier.getLocation().getId()
                    .equals(location.getId())) {

                throw new ResourceAlreadyExistsException(
                        "Location identifier already exists: "
                                + normalizedCode
                );
            }
        }

        // Determine identifiers that must be removed
        List<LocationIdentifier> identifiersToRemove =
                location.getIdentifiers().stream()
                        .filter(identifier ->
                                !requestedKeys.contains(
                                        createIdentifierKey(
                                                identifier.getCodeType().name(),
                                                normalizeCode(
                                                        identifier.getCode()
                                                )
                                        )
                                )
                        )
                        .toList();

        // Remove through entity relationship helper
        identifiersToRemove.forEach(location::removeIdentifier);

        // Add only identifiers that are not already associated
        for (LocationIdentifierRequest request : requestedIdentifiers) {

            String normalizedCode = normalizeCode(request.getCode());

            boolean alreadyPresent = location.getIdentifiers()
                    .stream()
                    .anyMatch(identifier ->
                            identifier.getCodeType()
                                    == request.getCodeType()
                                    && normalizeCode(identifier.getCode())
                                    .equals(normalizedCode)
                    );

            if (!alreadyPresent) {

                LocationIdentifier identifier =
                        locationMapper.toIdentifierEntity(request);

                identifier.setCode(normalizedCode);

                location.addIdentifier(identifier);
            }
        }
    }

    // CODE NORMALIZATION
    private String normalizeCode(String code) {

        return code.trim().toUpperCase(Locale.ROOT);
    }

    // COMPOSITE IDENTIFIER KEY
    private String createIdentifierKey(
            String codeType,
            String code
    ) {

        return codeType + ":" + code;
    }
}
