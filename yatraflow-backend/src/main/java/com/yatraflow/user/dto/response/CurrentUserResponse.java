package com.yatraflow.user.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CurrentUserResponse(

        Long userId,
        String firstName,
        String lastName,
        String email,
        List<String> roles
) {
}
