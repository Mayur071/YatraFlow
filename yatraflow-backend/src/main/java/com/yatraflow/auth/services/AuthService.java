package com.yatraflow.auth.services;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;

public interface AuthService {

    // // ========= Registration =========
    RegisterResponse register(RegisterRequest request);
}
