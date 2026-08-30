package com.yatraflow.Temp;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/temp")
public class TestProtectedController {

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(
            Authentication authentication) {

        return ResponseEntity.ok(
                Map.of(
                        "message", "JWT authentication successful",
                        "username", authentication.getName(),
                        "authorities", authentication.getAuthorities()
                )
        );
    }

}
