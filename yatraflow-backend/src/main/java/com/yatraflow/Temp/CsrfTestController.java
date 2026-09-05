package com.yatraflow.Temp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/csrf-test")
@Slf4j
public class CsrfTestController {


    @PostMapping
    public ResponseEntity<String> testCsrf() {

        log.info("CSRF protected test endpoint accessed successfully");

        return ResponseEntity.ok("CSRF validation successful");
    }
}

