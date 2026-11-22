package com.yatraflow.admin.dto.request;

import com.yatraflow.admin.enums.LocationType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateLocationRequest {

    @NotBlank(message = "Location name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 200)
    private String displayName;

    @NotNull(message = "Location type is required")
    private LocationType type;

    @Size(max = 100)
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    @Size(min = 2, max = 2)
    private String countryCode;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    @Size(max = 50)
    private String timezone;

    private Long parentLocationId;

    private List<LocationIdentifierRequest> identifiers;

}
