package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Book is already returned")
public class BorrowedBookAlreadyReturnedException extends RuntimeException {
}
