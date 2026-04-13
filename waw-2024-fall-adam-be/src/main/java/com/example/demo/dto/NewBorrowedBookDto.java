package com.example.demo.dto;

import java.time.LocalDate;

public record NewBorrowedBookDto(
        Integer officeId,
        LocalDate returnDate) {

}
