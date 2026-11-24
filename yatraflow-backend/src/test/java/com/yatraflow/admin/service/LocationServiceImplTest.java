package com.yatraflow.admin.service;

import com.yatraflow.admin.dto.response.LocationResponse;
import com.yatraflow.admin.entity.Location;
import com.yatraflow.admin.mapper.LocationMapper;
import com.yatraflow.admin.repository.LocationIdentifierRepository;
import com.yatraflow.admin.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
}
