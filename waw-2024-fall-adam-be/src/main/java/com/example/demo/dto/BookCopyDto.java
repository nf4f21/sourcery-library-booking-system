package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record BookCopyDto(
        @NotNull
        Integer bookCopyId,
        @NotNull(message = "The office ID cannot be null")
        Integer officeId,
        @NotNull(message = "The book ID cannot be null")
        Integer bookId,
        boolean isAvailable
) {
}