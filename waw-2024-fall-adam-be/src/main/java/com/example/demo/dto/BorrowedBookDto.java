package com.example.demo.dto;

import com.example.demo.model.BorrowStatus;

import java.time.LocalDate;

public record BorrowedBookDto(
        Integer borrowedId,
        Integer userId,
        Integer bookCopyId,
        String title,
        String author,
        byte[] coverImage,
        String officeName,
        BorrowStatus status,
        LocalDate borrowedFrom,
        LocalDate returnDate
) {
}
