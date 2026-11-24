package com.yatraflow.admin.location.dto.response;

import com.yatraflow.admin.location.enums.LocationCodeType;

public record LocationIdentifierResponse(

        Long id,
        LocationCodeType codeType,
        String code
) {


}
