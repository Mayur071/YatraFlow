package com.yatraflow.admin.dto.response;

import com.yatraflow.admin.enums.LocationCodeType;
import com.yatraflow.admin.enums.LocationType;

public record LocationIdentifierResponse(

        Long id,
        LocationCodeType codeType,
        String code
) {


}
