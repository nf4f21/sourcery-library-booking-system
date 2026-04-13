package com.example.demo.dto;

public record UserDto(
        Integer userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String defaultOfficeName

) {
}
