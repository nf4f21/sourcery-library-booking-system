package com.example.demo.dto;

public record BookCopyAvailabilityInOfficeDto(
        BasicOfficeDto basicOffice,
        Integer copiesAvailable,
        String address
) {
}
