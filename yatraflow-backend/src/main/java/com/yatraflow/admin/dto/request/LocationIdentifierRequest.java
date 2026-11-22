package com.yatraflow.admin.dto.request;

import com.yatraflow.admin.enums.LocationCodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationIdentifierRequest {


    @NotNull(message = "Code type is required")
    private LocationCodeType codeType;

    @NotBlank(message = "Location code is required")
    @Size(max = 20)
    private String code;
}
