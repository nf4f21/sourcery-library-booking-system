package com.example.demo.registration;

import com.example.demo.controller.base.BaseControllerTest;
import com.example.demo.dto.NewUserDto;
import com.example.demo.model.RoleEntity;
import com.example.demo.model.UserEntity;
import com.example.demo.registration.util.AuthenticationApiTestClient;
import com.example.demo.registration.util.AuthenticationDataFactory;
import com.example.demo.repository.UserRepository;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("local")
class AuthenticationControllerRegisterTest extends BaseControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnSuccessWhenRegisterUserWithValidRegisterRequest() {
        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();

        AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.OK.value())
                .body("userId", notNullValue())
                .body("email", equalTo(registerRequest.email()))
                .body("firstName", equalTo(registerRequest.firstName()))
                .body("lastName", equalTo(registerRequest.lastName()))
                .body("phoneNumber", equalTo(registerRequest.phoneNumber()))
                .body("defaultOfficeName", equalTo(registerRequest.defaultOfficeName()));

        UserEntity savedUser = userRepository.findByEmail(registerRequest.email())
                .orElseThrow(() -> new AssertionError("User not found in database"));

        Assertions.assertNotNull(savedUser.getUserId());
        Assertions.assertEquals(registerRequest.email(), savedUser.getEmail());
        Assertions.assertEquals(registerRequest.firstName(), savedUser.getFirstName());
        Assertions.assertEquals(registerRequest.lastName(), savedUser.getLastName());
        Assertions.assertEquals(registerRequest.phoneNumber(), savedUser.getPhoneNumber());
        Assertions.assertEquals(registerRequest.defaultOfficeName(), savedUser.getDefaultOffice().getName());
        List<String> roleUser = savedUser.getRoles().stream().map(RoleEntity::getName).toList();
        Assertions.assertEquals("USER", roleUser.getFirst());
    }

    @Test
    void shouldReturnConflictStatusWithMessageWhenEmailIsAlreadyTakenWhenRegisterUser() {
        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();

        AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.OK.value())
                .body("userId", notNullValue())
                .body("email", equalTo(registerRequest.email()))
                .body("firstName", equalTo(registerRequest.firstName()))
                .body("lastName", equalTo(registerRequest.lastName()))
                .body("phoneNumber", equalTo(registerRequest.phoneNumber()))
                .body("defaultOfficeName", equalTo(registerRequest.defaultOfficeName()));

        AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.CONFLICT.value())
                .body("info", equalTo(registerRequest.email() + " this email is already in use"));
    }

    @Test
    void shouldReturnNotFoundStatusWithMessageWhenOfficeNotFoundWhenRegisterUser() {
        NewUserDto registerRequest = AuthenticationDataFactory.getRegisterRequest();
        String notExistentOffice = "NotExistentOffice";
        NewUserDto userWithNotExistsOffice = new NewUserDto(registerRequest.email(), registerRequest.firstName(),
                registerRequest.lastName(), registerRequest.phoneNumber(), registerRequest.password(),
                notExistentOffice);

        AuthenticationApiTestClient.register(userWithNotExistsOffice)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("info", equalTo("Office not found"));
    }

    @Test
    void shouldReturnNotFoundStatusWithMessageWhenPutInvalidValuesWhenRegisterUser() {
        NewUserDto registerRequest = new NewUserDto(" ", " ", " ", " ",
                " ", " ");

        String expectedMessage = "This is not a correct email. | The lastName can not be empty. |" +
                " The phoneNumber can not be empty. | The password must be between 8-15 characters. |" +
                " The password can not be empty. | The email can not be empty. | The firstName can not be empty.";
        String[] expectedMessages = expectedMessage.split(" \\| ");

        ValidatableResponse response = AuthenticationApiTestClient.register(registerRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());

        String responseMessage = response.extract().path("info");
        String[] actualMessages = responseMessage.split(" \\| ");

        List<String> expectedMessagesList = Arrays.stream(expectedMessages).sorted().toList();
        List<String> actualMessagesList = Arrays.stream(actualMessages).sorted().toList();

        Assertions.assertEquals(expectedMessagesList, actualMessagesList);
    }
}