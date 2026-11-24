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
import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationIdentifierRepository locationIdentifierRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl locationService;

    private LocationIdentifierRequest createIdentifierRequest(
            LocationCodeType codeType,
            String code
    ) {
        LocationIdentifierRequest request = new LocationIdentifierRequest();
        request.setCodeType(codeType);
        request.setCode(code);
        return request;
    }


    @Test
    void shouldReturnLocationWhenLocationExists() {


        Long locationId = 1L;

        Location location = new Location();

        location.setId(locationId);
        location.setName("Ahmedabad");

        LocationResponse expectedResponse = mock(LocationResponse.class);

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
;
        when(locationMapper.toResponse(location)).thenReturn(expectedResponse);

        //ACT
        LocationResponse actualResponse = locationService.getLocationById(locationId);

        assertNotNull(actualResponse);
        assertSame(expectedResponse, actualResponse);

        verify(locationRepository).findById(locationId);
        verify(locationMapper).toResponse(location);
    }

    @Test
    void shouldThrowExceptionWhenLocationDoesNotExist() {

        // Arrange
        Long locationId = 99L;

        when(locationRepository.findById(locationId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> locationService.getLocationById(locationId)
        );

        verify(locationRepository).findById(locationId);
        verify(locationMapper, never()).toResponse(any());
    }


    @Test
    void shouldCreateLocationSuccessfully() {

        // Arrange
        CreateLocationRequest request = new CreateLocationRequest();

        Location savedLocation = new Location();
        savedLocation.setId(1L);

        LocationResponse expectedResponse = mock(LocationResponse.class);

        when(locationRepository.save(any(Location.class)))
                .thenReturn(savedLocation);

        when(locationMapper.toResponse(savedLocation))
                .thenReturn(expectedResponse);

        // Act
        LocationResponse actualResponse =
                locationService.createLocation(request);

        // Assert
        assertNotNull(actualResponse);
        assertSame(expectedResponse, actualResponse);

        verify(locationMapper).updateLocationFields(
                any(Location.class),
                eq(request)
        );

        verify(locationRepository).save(any(Location.class));
        verify(locationMapper).toResponse(savedLocation);
    }

    @Test
    void shouldThrowExceptionWhenIdentifierAlreadyExists() {

        // Arrange
        CreateLocationRequest request = new CreateLocationRequest();

        LocationIdentifierRequest identifierRequest =
                new LocationIdentifierRequest();

        identifierRequest.setCodeType(LocationCodeType.IATA);
        identifierRequest.setCode("AMD");

        request.setIdentifiers(List.of(identifierRequest));

        Location existingLocation = new Location();
        existingLocation.setId(10L);

        LocationIdentifier existingIdentifier = new LocationIdentifier();
        existingIdentifier.setCodeType(LocationCodeType.IATA);
        existingIdentifier.setCode("AMD");
        existingIdentifier.setLocation(existingLocation);

        when(locationIdentifierRepository.findByCodeAndCodeType(
                "AMD", LocationCodeType.IATA
        )).thenReturn(Optional.of(existingIdentifier));

        // Act & Assert
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> locationService.createLocation(request)
        );

        verify(locationIdentifierRepository)
                .findByCodeAndCodeType("AMD", LocationCodeType.IATA);

        verify(locationRepository, never()).save(any(Location.class));
    }



        @Test
        void shouldThrowExceptionWhenDuplicateIdentifiersExistInRequest() {

            // Arrange
            CreateLocationRequest request = new CreateLocationRequest();

            LocationIdentifierRequest identifier1 = new LocationIdentifierRequest();
            identifier1.setCodeType(LocationCodeType.IATA);
            identifier1.setCode("AMD");

            LocationIdentifierRequest identifier2 = new LocationIdentifierRequest();
            identifier2.setCodeType(LocationCodeType.IATA);
            identifier2.setCode("AMD");

            request.setIdentifiers(List.of(identifier1, identifier2));

            LocationIdentifier mappedIdentifier = new LocationIdentifier();

            when(locationMapper.toIdentifierEntity(identifier1))
                    .thenReturn(mappedIdentifier);

            // Act & Assert
            assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> locationService.createLocation(request)
            );

            verify(locationIdentifierRepository, times(1))
                    .findByCodeAndCodeType("AMD", LocationCodeType.IATA);

            verify(locationRepository, never()).save(any(Location.class));
        }

    @Test
    void shouldSynchronizeLocationIdentifiersSuccessfully() {

        // Arrange
        Long locationId = 1L;

        Location location = new Location();
        location.setId(locationId);
        location.setName("Ahmedabad");
        location.setType(LocationType.AIRPORT);

        LocationIdentifier existingIata = new LocationIdentifier();
        existingIata.setCodeType(LocationCodeType.IATA);
        existingIata.setCode("AMD");

        LocationIdentifier existingIcao = new LocationIdentifier();
        existingIcao.setCodeType(LocationCodeType.ICAO);
        existingIcao.setCode("VAAH");

        location.addIdentifier(existingIata);
        location.addIdentifier(existingIcao);

        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setName("Ahmedabad Updated");
        request.setType(LocationType.AIRPORT);
        request.setCountry("India");
        request.setIdentifiers(List.of(
                createIdentifierRequest(LocationCodeType.IATA, "AMD"),
                createIdentifierRequest(LocationCodeType.RAILWAY_CODE, "ADI")
        ));

        LocationIdentifier newIdentifier = new LocationIdentifier();
        newIdentifier.setCodeType(LocationCodeType.RAILWAY_CODE);
        newIdentifier.setCode("ADI");


        when(locationRepository.findById(locationId))
                .thenReturn(Optional.of(location));

        when(locationIdentifierRepository.findByCodeAndCodeType(
                "AMD", LocationCodeType.IATA))
                .thenReturn(Optional.empty());

        when(locationIdentifierRepository.findByCodeAndCodeType(
                "ADI", LocationCodeType.RAILWAY_CODE))
                .thenReturn(Optional.empty());

        when(locationMapper.toIdentifierEntity(
                request.getIdentifiers().get(1)))
                .thenReturn(newIdentifier);

        when(locationRepository.save(location))
                .thenReturn(location);

        when(locationMapper.toResponse(location))
                .thenReturn(null);

        // Act
        LocationResponse actualResponse =
                locationService.updateLocation(locationId, request);

        // Assert
        assertNull(actualResponse);

        assertEquals(2, location.getIdentifiers().size());

        assertTrue(location.getIdentifiers().stream()
                .anyMatch(identifier ->
                        identifier.getCodeType() == LocationCodeType.IATA
                                && identifier.getCode().equals("AMD")));

        assertTrue(location.getIdentifiers().stream()
                .anyMatch(identifier ->
                        identifier.getCodeType() == LocationCodeType.RAILWAY_CODE
                                && identifier.getCode().equals("ADI")));

        assertFalse(location.getIdentifiers().stream()
                .anyMatch(identifier ->
                        identifier.getCodeType() == LocationCodeType.ICAO
                                && identifier.getCode().equals("VAAH")));

        verify(locationRepository).save(location);
    }

    @Test
    void shouldRejectInactiveParentLocation() {

        // Arrange
        Long locationId = 1L;
        Long parentId = 2L;

        Location location = new Location();
        location.setId(locationId);
        location.setType(LocationType.AIRPORT);

        Location inactiveParent = new Location();
        inactiveParent.setId(parentId);
        inactiveParent.setType(LocationType.CITY);
        inactiveParent.setActive(false);

        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setName("Ahmedabad Airport");
        request.setType(LocationType.AIRPORT);
        request.setCountry("India");
        request.setParentLocationId(parentId);
        request.setIdentifiers(List.of());

        when(locationRepository.findById(locationId))
                .thenReturn(Optional.of(location));

        when(locationRepository.findById(parentId))
                .thenReturn(Optional.of(inactiveParent));

        // Act & Assert
        assertThrows(
                BusinessException.class,
                () -> locationService.updateLocation(locationId, request)
        );

        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void shouldPreventDeactivationWhenActiveChildrenExist() {

        // Arrange
        Long locationId = 1L;

        Location location = new Location();
        location.setId(locationId);
        location.setActive(true);

        when(locationRepository.findById(locationId))
                .thenReturn(Optional.of(location));

        when(locationRepository.existsByParentLocation_IdAndActiveTrue(locationId))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                BusinessException.class,
                () -> locationService.deactivateLocation(locationId)
        );

        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void shouldSearchLocationsWithPagination() {

        // Arrange
        Location location = new Location();
        location.setId(1L);
        location.setName("Ahmedabad");

        LocationResponse response = mock(LocationResponse.class);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Location> locationPage = new PageImpl<>(
                List.of(location),
                pageable,
                1
        );

        when(locationRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(locationPage);

        when(locationMapper.toResponse(location))
                .thenReturn(response);

        // Act
        Page<LocationResponse> result = locationService.searchLocations(
                "Ahmedabad",
                LocationType.CITY,
                "India",
                "Gujarat",
                true,
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response, result.getContent().get(0));
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        verify(locationRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }
}
