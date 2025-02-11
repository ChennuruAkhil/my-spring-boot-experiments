package com.example.featuretoggle;

import com.example.featuretoggle.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class TogglzApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogglzApplication.class, args);
    }
}
