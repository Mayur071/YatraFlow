package com.yatraflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    //For configuration of users password
    private static final int BCRYPT_STRENGTH = 12;

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

}
