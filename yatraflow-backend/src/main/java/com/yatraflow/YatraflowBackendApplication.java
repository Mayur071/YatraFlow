package com.yatraflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YatraflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YatraflowBackendApplication.class, args);
	}

}
