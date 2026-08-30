package com.yatraflow.auth.services.login;

import com.yatraflow.auth.dto.response.LoginResponse;

public record LoginResult(
        LoginResponse loginResponse,
        String accessToken
) {
}

