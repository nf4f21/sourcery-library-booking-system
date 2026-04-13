package com.example.demo.registration;

import com.example.demo.DemoApplication;
import com.example.demo.dto.NewUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.security.jwt.JwtAuthenticationResponse;
import com.example.demo.security.jwt.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = DemoApplication.PATH_V1 + "/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/register")
    public UserDto register(@RequestBody @Valid NewUserDto newUserDto) {
        return authenticationService.registerUser(newUserDto);
    }

    @PostMapping(value = "/authorize")
    public JwtAuthenticationResponse loginUser(@RequestBody LoginUser loginUser) {
        return authenticationService.login(loginUser);
    }

    @PostMapping(value = "/refresh")
    public JwtAuthenticationResponse refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refreshToken(refreshTokenRequest);
    }

}
