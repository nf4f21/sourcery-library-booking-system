package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewUserDto(

        @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "This is not a correct email.")
        @NotBlank(message = "The email can not be empty.")
        String email,
        @NotBlank(message = "The firstName can not be empty.")
        String firstName,
        @NotBlank(message = "The lastName can not be empty.")
        String lastName,
        @NotBlank(message = "The phoneNumber can not be empty.")
        String phoneNumber,
        @NotBlank(message = "The password can not be empty.")
        @Size(min = 8, max = 15, message = "The password must be between 8-15 characters.")
        String password,
        @NotNull(message = "The officeName can not be empty.")
        String defaultOfficeName) {

}
