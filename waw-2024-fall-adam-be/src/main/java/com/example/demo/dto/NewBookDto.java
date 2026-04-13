package com.example.demo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record NewBookDto(
        @NotNull(message = "You need to upload coverImage")
        MultipartFile coverImage,
        @NotBlank(message = "The tittle can not be empty")
        String title,
        @NotBlank(message = "The author can not be empty")
        String author,
        @NotBlank(message = "The description can not be empty")
        String description,
        @NotBlank(message = "The format can not be empty")
        String format,
        @NotNull(message = "The numberOfPages can not be empty")
        Integer numberOfPages,
        @NotNull(message = "The publicationDate can not be empty")
        LocalDate publicationDate,
        @NotBlank(message = "The publisher can not be empty")
        String publisher,
        @NotBlank(message = "The isbn can not be empty")
        String isbn,
        @NotBlank(message = "The editionLanguage can not be empty")
        String editionLanguage,
        @NotBlank(message = "The series can not be empty")
        String series,
        @NotBlank(message = "The category can not be empty")
        String category,
        @NotNull(message = "The bookCopies cannot be empty")
        List<NewBookCopyDto> newBookCopies
) {
        private static final ObjectMapper objectMapper = new ObjectMapper();

}
