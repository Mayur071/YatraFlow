package com.yatraflow.auth.services.login;

import com.yatraflow.auth.dto.request.LoginRequest;
import com.yatraflow.auth.dto.response.LoginResponse;

public interface LoginService {

    LoginResult login(LoginRequest loginRequest);


}
