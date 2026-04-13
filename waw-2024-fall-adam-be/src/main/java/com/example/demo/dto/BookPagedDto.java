package com.example.demo.dto;

import java.util.List;

public record BookPagedDto(
        List<BookPreviewDto> books,
        Long total,
        Integer totalPages
) {
}
