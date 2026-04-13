package com.example.demo.dto;

public record BookPreviewDto(
        Integer bookId,
        String title,
        String author,
        byte[] coverImage,
        Boolean isAvailable
) {
}
