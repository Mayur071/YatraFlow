package com.yatraflow.security.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.domain")
public class AdminProperties {

    private String email;

    private String password;
}
