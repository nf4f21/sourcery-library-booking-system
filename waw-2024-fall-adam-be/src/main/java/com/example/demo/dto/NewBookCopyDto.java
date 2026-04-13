package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record NewBookCopyDto(
        @NotNull(message = "The office ID cannot be null")
        Integer officeId,
        @NotNull(message = "The copy count cannot be null")
        @Min(value = 0, message = "The copy count cannot be negative")
        Integer copyCount,
        Integer bookId
) {

}
