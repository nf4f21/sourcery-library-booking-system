package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static final String PATH_V1 = "/api/v1";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
