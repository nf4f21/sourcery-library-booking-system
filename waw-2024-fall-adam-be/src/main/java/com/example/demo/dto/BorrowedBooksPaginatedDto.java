package com.example.demo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BorrowedBooksPaginatedDto(
        Long borrowedBooksCount,
        List<BorrowedBookDto> borrowedBooks
) {
}
