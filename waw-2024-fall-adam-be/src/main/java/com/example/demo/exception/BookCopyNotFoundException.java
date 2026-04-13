package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BookCopyNotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE_BOOK_ID = "Not found book copy with bookId: %s or it is not available in officeId: %s";
    private static final String MESSAGE_TEMPLATE_BOOK_COPY_ID = "Not found book copy with bookCopyId: %s";

    public BookCopyNotFoundException(Integer bookId, Integer officeId) {
        super(MESSAGE_TEMPLATE_BOOK_ID.formatted(bookId, officeId));
    }

    public BookCopyNotFoundException(Integer bookCopyId) {
        super(MESSAGE_TEMPLATE_BOOK_COPY_ID.formatted(bookCopyId));
    }
}
