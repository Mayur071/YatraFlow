package com.yatraflow.admin.location.controller;

import com.yatraflow.admin.location.dto.request.CreateLocationRequest;
import com.yatraflow.admin.location.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.location.dto.response.LocationResponse;
import com.yatraflow.admin.location.enums.LocationType;
import com.yatraflow.admin.location.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody CreateLocationRequest request) {

        log.info("Creating a new location");

        LocationResponse response = locationService.createLocation(request);

        return ResponseEntity
                .created(URI.create("/api/admin/locations/" + response.id()))
                .body(response);
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestBody UpdateLocationRequest request) {

        log.info("Updating location with ID: {}", locationId);

        LocationResponse response =
                locationService.updateLocation(locationId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponse> getLocationById(
            @PathVariable Long locationId) {

        log.info("Fetching location with ID: {}", locationId);

        LocationResponse response =
                locationService.getLocationById(locationId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<LocationResponse>> searchLocations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocationType type,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        log.info("Searching locations");

        Page<LocationResponse> response = locationService.searchLocations(
                search,
                type,
                country,
                state,
                active,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{locationId}/activate")
    public ResponseEntity<LocationResponse> activateLocation(
            @PathVariable Long locationId) {

        log.info("Activating location with ID: {}", locationId);

        LocationResponse response =
                locationService.activateLocation(locationId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{locationId}/deactivate")
    public ResponseEntity<LocationResponse> deactivateLocation(
            @PathVariable Long locationId) {

        log.info("Deactivating location with ID: {}", locationId);

        LocationResponse response =
                locationService.deactivateLocation(locationId);

        return ResponseEntity.ok(response);
    }
}