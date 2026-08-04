package com.yatraflow.auth.services.register;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;

public interface RegisterService {

    // // ========= Registration =========
    RegisterResponse register(RegisterRequest request);
}
