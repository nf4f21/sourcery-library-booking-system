package com.example.demo.controller.base;

import com.example.demo.dto.NewUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.registration.LoginUser;
import com.example.demo.registration.util.AuthenticationApiTestClient;
import com.example.demo.registration.util.AuthenticationDataFactory;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;

/**
 * Shared logic for all controller tests, that additionally makes all requests
 * made in tests automatically authenticated
 */
public class BaseAuthenticatedControllerTest extends BaseControllerTest {
    private UserDto user;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();
        user = AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDto.class);


        var loginUser = new LoginUser(user.email(), registerRequest.password());
        var authToken = AuthenticationApiTestClient.login(loginUser)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setConfig(RestAssuredConfig.config().headerConfig(
                        HeaderConfig.headerConfig().overwriteHeadersWithName("Authorization")))
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
    }

    protected UserDto currentUser() {
        return user;
    }
}
