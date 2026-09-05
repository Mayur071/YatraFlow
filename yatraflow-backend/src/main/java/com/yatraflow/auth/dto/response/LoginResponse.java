package com.yatraflow.auth.dto.response;

import lombok.*;

import java.util.List;

@Builder
public record LoginResponse (

        Long userId,

        String firstName,

        String lastName,

        String email,

        List<String> roles,

        String tokenType,

        Long expiresIn,

        Long refreshExpiresIn
)

{

}
