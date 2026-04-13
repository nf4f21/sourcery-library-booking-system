package com.example.demo.dto;

import java.time.LocalDate;

public record UpdateBorrowedBookDto(
        LocalDate returnDate
) {
}
