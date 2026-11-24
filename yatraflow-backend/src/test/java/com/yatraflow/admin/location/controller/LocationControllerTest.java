package com.yatraflow.admin.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatraflow.admin.location.dto.request.CreateLocationRequest;
import com.yatraflow.admin.location.dto.request.UpdateLocationRequest;
import com.yatraflow.admin.location.dto.response.LocationResponse;
import com.yatraflow.admin.location.enums.LocationType;
import com.yatraflow.admin.location.service.LocationService;
import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceNotFoundException;
import com.yatraflow.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocationService locationService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private LocationResponse createLocationResponse(Long id) {
        return new LocationResponse(
                id,
                "Ahmedabad",
                "Ahmedabad",
                LocationType.CITY,
                "Gujarat",
                "India",
                "IN",
                new BigDecimal("23.0225"),
                new BigDecimal("72.5714"),
                "Asia/Kolkata",
                null,
                null,
                Collections.emptyList(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private CreateLocationRequest createValidRequest() {
        CreateLocationRequest request = new CreateLocationRequest();

        request.setName("Ahmedabad");
        request.setDisplayName("Ahmedabad");
        request.setType(LocationType.CITY);
        request.setState("Gujarat");
        request.setCountry("India");
        request.setCountryCode("IN");
        request.setLatitude(new BigDecimal("23.0225"));
        request.setLongitude(new BigDecimal("72.5714"));
        request.setTimezone("Asia/Kolkata");
        request.setIdentifiers(Collections.emptyList());

        return request;
    }

    private UpdateLocationRequest createValidUpdateRequest() {
        UpdateLocationRequest request = new UpdateLocationRequest();

        request.setName("Ahmedabad Updated");
        request.setDisplayName("Ahmedabad Updated");
        request.setType(LocationType.CITY);
        request.setState("Gujarat");
        request.setCountry("India");
        request.setCountryCode("IN");
        request.setLatitude(new BigDecimal("23.0225"));
        request.setLongitude(new BigDecimal("72.5714"));
        request.setTimezone("Asia/Kolkata");
        request.setIdentifiers(Collections.emptyList());

        return request;
    }

    // 1. Create Location Successfully

    @Test
    void shouldCreateLocationSuccessfully() throws Exception {

        LocationResponse response = createLocationResponse(1L);

        when(locationService.createLocation(any(CreateLocationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/admin/locations/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ahmedabad"))
                .andExpect(jsonPath("$.type").value("CITY"));

        verify(locationService, times(1))
                .createLocation(any(CreateLocationRequest.class));
    }

    // 2. Create Location With Invalid Request

    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {

        CreateLocationRequest request = createValidRequest();
        request.setName("");

        mockMvc.perform(post("/api/admin/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(locationService, never())
                .createLocation(any(CreateLocationRequest.class));
    }

    // 3. Update Location Successfully

    @Test
    void shouldUpdateLocationSuccessfully() throws Exception {

        LocationResponse response = createLocationResponse(1L);

        when(locationService.updateLocation(
                eq(1L),
                any(UpdateLocationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/admin/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                createValidUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ahmedabad"));

        verify(locationService, times(1))
                .updateLocation(eq(1L), any(UpdateLocationRequest.class));
    }

    // 4. Update Location With Invalid Request

    @Test
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {

        UpdateLocationRequest request = createValidUpdateRequest();
        request.setName("");
        request.setIdentifiers(null);

        mockMvc.perform(put("/api/admin/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(locationService, never())
                .updateLocation(anyLong(), any(UpdateLocationRequest.class));
    }

    // 5. Get Location By ID Successfully

    @Test
    void shouldGetLocationByIdSuccessfully() throws Exception {

        LocationResponse response = createLocationResponse(1L);

        when(locationService.getLocationById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ahmedabad"))
                .andExpect(jsonPath("$.type").value("CITY"));

        verify(locationService, times(1))
                .getLocationById(1L);
    }

    // 6. Get Nonexistent Location

    @Test
    void shouldReturnNotFoundWhenLocationDoesNotExist() throws Exception {

        when(locationService.getLocationById(999L))
                .thenThrow(new ResourceNotFoundException("Location not found"));

        mockMvc.perform(get("/api/admin/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1))
                .getLocationById(999L);
    }

    // 7. Search Locations With Pagination

    @Test
    void shouldSearchLocationsWithPagination() throws Exception {

        LocationResponse response = createLocationResponse(1L);

        Page<LocationResponse> page = new PageImpl<>(
                Collections.singletonList(response),
                PageRequest.of(0, 10),
                1
        );

        when(locationService.searchLocations(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/locations")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Ahmedabad"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(locationService, times(1))
                .searchLocations(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(Pageable.class)
                );
    }

    // 8. Search Locations With Filters

    @Test
    void shouldSearchLocationsWithFilters() throws Exception {

        Page<LocationResponse> page = new PageImpl<>(
                Collections.singletonList(createLocationResponse(1L))
        );

        when(locationService.searchLocations(
                eq("Ahmedabad"),
                eq(LocationType.CITY),
                eq("India"),
                eq("Gujarat"),
                eq(true),
                any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/locations")
                        .param("search", "Ahmedabad")
                        .param("type", "CITY")
                        .param("country", "India")
                        .param("state", "Gujarat")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ahmedabad"));

        verify(locationService, times(1))
                .searchLocations(
                        eq("Ahmedabad"),
                        eq(LocationType.CITY),
                        eq("India"),
                        eq("Gujarat"),
                        eq(true),
                        any(Pageable.class)
                );
    }

    // 9. Activate Location Successfully

    @Test
    void shouldActivateLocationSuccessfully() throws Exception {

        LocationResponse response = createLocationResponse(1L);

        when(locationService.activateLocation(1L))
                .thenReturn(response);

        mockMvc.perform(patch("/api/admin/locations/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));

        verify(locationService, times(1))
                .activateLocation(1L);
    }

    // 10. Activate Nonexistent Location

    @Test
    void shouldReturnNotFoundWhenActivatingNonexistentLocation() throws Exception {

        when(locationService.activateLocation(999L))
                .thenThrow(new ResourceNotFoundException("Location not found"));

        mockMvc.perform(patch("/api/admin/locations/999/activate"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1))
                .activateLocation(999L);
    }

    // 11. Deactivate Location Successfully

    @Test
    void shouldDeactivateLocationSuccessfully() throws Exception {

        LocationResponse response = new LocationResponse(
                1L,
                "Ahmedabad",
                "Ahmedabad",
                LocationType.CITY,
                "Gujarat",
                "India",
                "IN",
                new BigDecimal("23.0225"),
                new BigDecimal("72.5714"),
                "Asia/Kolkata",
                null,
                null,
                Collections.emptyList(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(locationService.deactivateLocation(1L))
                .thenReturn(response);

        mockMvc.perform(patch("/api/admin/locations/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(false));

        verify(locationService, times(1))
                .deactivateLocation(1L);
    }

    // 12. Deactivation Business Rule Failure

    @Test
    void shouldReturnBadRequestWhenDeactivationFailsBusinessRule() throws Exception {

        when(locationService.deactivateLocation(1L))
                .thenThrow(new BusinessException(
                        "Location cannot be deactivated"
                ));

        mockMvc.perform(patch("/api/admin/locations/1/deactivate"))
                .andExpect(status().isBadRequest());

        verify(locationService, times(1))
                .deactivateLocation(1L);
    }
}