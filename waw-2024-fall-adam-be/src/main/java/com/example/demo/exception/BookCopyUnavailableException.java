package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BookCopyUnavailableException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Book copy with ID of %s is not available";

    public BookCopyUnavailableException(Integer bookCopyId) {
        super(MESSAGE_TEMPLATE.formatted(bookCopyId));
    }
}
