package com.yatraflow.admin.location.service;

import com.yatraflow.admin.location.dto.request.CreateLocationRequest;
import com.yatraflow.admin.location.dto.request.LocationIdentifierRequest;
import com.yatraflow.admin.location.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.location.dto.response.LocationResponse;
import com.yatraflow.admin.location.entity.Location;
import com.yatraflow.admin.location.entity.LocationIdentifier;
import com.yatraflow.admin.location.enums.LocationCodeType;
import com.yatraflow.admin.location.enums.LocationType;
import com.yatraflow.admin.location.mapper.LocationMapper;
import com.yatraflow.admin.location.repository.LocationIdentifierRepository;
import com.yatraflow.admin.location.repository.LocationRepository;
import com.yatraflow.admin.location.repository.specification.LocationSpecification;
import com.yatraflow.exception.BusinessException;
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
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationIdentifierRepository locationIdentifierRepository;
    private final LocationMapper locationMapper;

    // =========================================================
    // CREATE LOCATION
    // =========================================================

    @Override
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

        validateAndAddIdentifiers(
                location,
                request.getIdentifiers()
        );

        Location savedLocation = locationRepository.save(location);

        return locationMapper.toResponse(savedLocation);
    }

    // =========================================================
    // UPDATE LOCATION
    // =========================================================

    @Override
    public LocationResponse updateLocation(
            Long locationId,
            UpdateLocationRequest request
    ) {

        Location location = findLocationById(locationId);

        locationMapper.updateLocationFields(location, request);

        if (location.getType() == LocationType.CITY) {

            if (request.getParentLocationId() != null) {

                throw new BusinessException(
                        "CITY locations cannot have a parent location."
                );
            }

            location.setParentLocation(null);

        } else if (request.getParentLocationId() != null) {

            Location parentLocation = findLocationById(
                    request.getParentLocationId()
            );

            validateParentLocation(parentLocation, locationId);

            location.setParentLocation(parentLocation);

        } else {

            location.setParentLocation(null);
        }

        synchronizeIdentifiers(
                location,
                request.getIdentifiers()
        );

        Location updatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(updatedLocation);
    }

    // =========================================================
    // GET LOCATION BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long locationId) {

        Location location = findLocationById(locationId);

        return locationMapper.toResponse(location);
    }

    // =========================================================
    // SEARCH LOCATIONS
    // =========================================================

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
                )
                .map(locationMapper::toResponse);
    }

    // =========================================================
    // ACTIVATE LOCATION
    // =========================================================

    @Override
    public LocationResponse activateLocation(Long locationId) {

        Location location = findLocationById(locationId);

        if (location.isActive()) {
            return locationMapper.toResponse(location);
        }

        location.setActive(true);

        Location activatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(activatedLocation);
    }

    // =========================================================
    // DEACTIVATE LOCATION
    // =========================================================

    @Override
    public LocationResponse deactivateLocation(Long locationId) {

        Location location = findLocationById(locationId);

        if (!location.isActive()) {
            return locationMapper.toResponse(location);
        }

        boolean hasActiveChildren =
                locationRepository.existsByParentLocation_IdAndActiveTrue(
                        locationId
                );

        if (hasActiveChildren) {

            throw new BusinessException(
                    "Location cannot be deactivated while active child locations exist."
            );
        }

        location.setActive(false);

        Location deactivatedLocation = locationRepository.save(location);

        return locationMapper.toResponse(deactivatedLocation);
    }



    // =========================================================
    // FIND LOCATION BY ID
    // =========================================================

    private Location findLocationById(Long locationId) {

        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with ID: " + locationId
                ));
    }

    // =========================================================
    // PARENT LOCATION VALIDATION
    // =========================================================

    private void validateParentLocation(
            Location parentLocation,
            Long currentLocationId
    ) {

        if (!parentLocation.isActive()) {

            throw new BusinessException(
                    "Inactive location cannot be assigned as a parent."
            );
        }

        if (currentLocationId != null
                && parentLocation.getId().equals(currentLocationId)) {

            throw new BusinessException(
                    "A location cannot be its own parent."
            );
        }

        if (parentLocation.getType() != LocationType.CITY) {

            throw new BusinessException(
                    "Only CITY locations can be assigned as parent locations."
            );
        }

        Location ancestor = parentLocation;

        while (ancestor != null) {

            if (currentLocationId != null
                    && ancestor.getId().equals(currentLocationId)) {

                throw new BusinessException(
                        "Circular location hierarchy is not allowed."
                );
            }

            ancestor = ancestor.getParentLocation();
        }
    }

    // =========================================================
    // CREATE IDENTIFIERS
    // =========================================================

    private void validateAndAddIdentifiers(
            Location location,
            List<LocationIdentifierRequest> identifierRequests
    ) {

        if (identifierRequests == null || identifierRequests.isEmpty()) {
            return;
        }

        Set<String> uniqueIdentifierKeys = new HashSet<>();

        for (LocationIdentifierRequest request : identifierRequests) {

            String normalizedCode = normalizeCode(request.getCode());

            String identifierKey = createIdentifierKey(
                    request.getCodeType(),
                    normalizedCode
            );

            if (!uniqueIdentifierKeys.add(identifierKey)) {

                throw new ResourceAlreadyExistsException(
                        "Duplicate identifier provided: " + normalizedCode
                );
            }

            validateIdentifierAvailability(
                    request.getCodeType(),
                    normalizedCode,
                    null
            );

            LocationIdentifier identifier =
                    locationMapper.toIdentifierEntity(request);

            identifier.setCode(normalizedCode);

            location.addIdentifier(identifier);
        }
    }

    // =========================================================
    // SYNCHRONIZE IDENTIFIERS
    // =========================================================

    private void synchronizeIdentifiers(
            Location location,
            List<LocationIdentifierRequest> identifierRequests
    ) {

        if (identifierRequests == null) {

            throw new BusinessException(
                    "Identifiers list must not be null."
            );
        }

        Set<String> requestedIdentifierKeys = new HashSet<>();

        for (LocationIdentifierRequest request : identifierRequests) {

            String normalizedCode = normalizeCode(request.getCode());

            String identifierKey = createIdentifierKey(
                    request.getCodeType(),
                    normalizedCode
            );

            if (!requestedIdentifierKeys.add(identifierKey)) {

                throw new ResourceAlreadyExistsException(
                        "Duplicate identifier provided: " + normalizedCode
                );
            }

            validateIdentifierAvailability(
                    request.getCodeType(),
                    normalizedCode,
                    location.getId()
            );
        }

        List<LocationIdentifier> identifiersToRemove =
                location.getIdentifiers()
                        .stream()
                        .filter(identifier -> {

                            String existingKey = createIdentifierKey(
                                    identifier.getCodeType(),
                                    normalizeCode(identifier.getCode())
                            );

                            return !requestedIdentifierKeys.contains(existingKey);
                        })
                        .toList();

        identifiersToRemove.forEach(location::removeIdentifier);

        for (LocationIdentifierRequest request : identifierRequests) {

            String normalizedCode = normalizeCode(request.getCode());

            boolean alreadyExists = location.getIdentifiers()
                    .stream()
                    .anyMatch(identifier ->
                            identifier.getCodeType() == request.getCodeType()
                                    && normalizeCode(identifier.getCode())
                                    .equals(normalizedCode)
                    );

            if (!alreadyExists) {

                LocationIdentifier identifier =
                        locationMapper.toIdentifierEntity(request);

                identifier.setCode(normalizedCode);

                location.addIdentifier(identifier);
            }
        }
    }

    // =========================================================
    // IDENTIFIER AVAILABILITY
    // =========================================================

    private void validateIdentifierAvailability(
            LocationCodeType codeType,
            String normalizedCode,
            Long currentLocationId
    ) {

        locationIdentifierRepository
                .findByCodeAndCodeType(normalizedCode, codeType)
                .ifPresent(existingIdentifier -> {

                    Long existingLocationId =
                            existingIdentifier.getLocation().getId();

                    if (currentLocationId == null
                            || !existingLocationId.equals(currentLocationId)) {

                        throw new ResourceAlreadyExistsException(
                                "Identifier already exists: "
                                        + normalizedCode
                                        + " ("
                                        + codeType
                                        + ")"
                        );
                    }
                });
    }

    // =========================================================
    // NORMALIZE IDENTIFIER CODE
    // =========================================================

    private String normalizeCode(String code) {

        if (code == null) {
            return null;
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }

    // =========================================================
    // CREATE IDENTIFIER KEY
    // =========================================================

    private String createIdentifierKey(
            LocationCodeType codeType,
            String normalizedCode
    ) {

        return codeType.name() + ":" + normalizedCode;
    }
}