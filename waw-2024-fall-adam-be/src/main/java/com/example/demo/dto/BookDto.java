package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record BookDto(
        Integer bookId,
        byte[] coverImage,
        String title,
        String author,
        String description,
        String format,
        Integer numberOfPages,
        LocalDate publicationDate,
        String publisher,
        String isbn,
        String editionLanguage,
        String series,
        String category,
        List<BookCopyDto> bookCopies
) {
}

