package com.example.demo.registration;

import com.example.demo.controller.base.BaseControllerTest;
import com.example.demo.dto.NewUserDto;
import com.example.demo.registration.util.AuthenticationApiTestClient;
import com.example.demo.registration.util.AuthenticationDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("local")
class AuthenticationControllerLoginTest extends BaseControllerTest {

    @Test
    void shouldReturnSuccessWhenLoginUserWithValidLoginRequest() {
        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();
        AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.OK.value());

        LoginUser loginRequest = AuthenticationDataFactory.getLoginRequest();

        AuthenticationApiTestClient.login(loginRequest)
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    void shouldReturnForbiddenStatusWithMessageWhenLoginUserWithInValidLoginRequest() {
        LoginUser loginRequest = AuthenticationDataFactory.getLoginRequest();

        AuthenticationApiTestClient.login(loginRequest)
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnSuccessWhenRefreshTokenWithValidLoginRequest() {
        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();
        AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.OK.value());

        LoginUser loginRequest = AuthenticationDataFactory.getLoginRequest();

        String refreshTokenResponse = AuthenticationApiTestClient.login(loginRequest)
                .statusCode(HttpStatus.OK.value())
                .extract().path("refreshToken");

        String refreshTokenRequest = AuthenticationDataFactory.getRefreshTokenRequest(refreshTokenResponse);
        AuthenticationApiTestClient.refresh(refreshTokenRequest)
                .statusCode(HttpStatus.OK.value());
    }
}