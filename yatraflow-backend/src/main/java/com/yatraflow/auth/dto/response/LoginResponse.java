package com.yatraflow.auth.dto.response;

import lombok.*;

@Builder
public record LoginResponse (

        Long userId,

        String firstName,

        String lastName,

        String email,

        String accessToken,

        String refreshToken,

        String tokenType,

        Long expiresIn
)

{

}
