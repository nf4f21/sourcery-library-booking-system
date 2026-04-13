package com.example.demo.registration.util;

import com.example.demo.dto.NewUserDto;
import com.example.demo.registration.LoginUser;

public class AuthenticationDataFactory {

    public static NewUserDto getRegisterRequest() {
        return new NewUserDto(
                "test@gmail.com",
                "testfirstname",
                "testlastname",
                "testphonenumber",
                "Passwordtest10!",
                "London");
    }

    public static LoginUser getLoginRequest() {
        return new LoginUser(
                "test@gmail.com",
                "Passwordtest10!");
    }

    public static String getRefreshTokenRequest(String refreshTokenResponse) {
        return String.format("{\"token\":\"%s\"}", refreshTokenResponse);
    }
}
