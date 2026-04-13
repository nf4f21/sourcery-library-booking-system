package com.example.demo.registration.util;

import com.example.demo.dto.NewUserDto;
import com.example.demo.registration.LoginUser;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class AuthenticationApiTestClient {

    public static ValidatableResponse register(NewUserDto registerRequest) {
        return given().contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then();
    }

    public static ValidatableResponse login(LoginUser loginRequest) {
        return given().contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/authorize")
                .then();
    }

    public static ValidatableResponse refresh(String refreshTokenRequest) {
        return given().contentType(ContentType.JSON)
                .body(refreshTokenRequest)
                .when()
                .post("/api/v1/auth/refresh")
                .then();
    }
}
