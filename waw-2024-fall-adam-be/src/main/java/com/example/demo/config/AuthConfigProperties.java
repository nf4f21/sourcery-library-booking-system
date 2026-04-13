package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


@ConfigurationProperties(prefix = "security.jwt.token")
public record AuthConfigProperties(
        String secret,
        Duration validity
) {
}
