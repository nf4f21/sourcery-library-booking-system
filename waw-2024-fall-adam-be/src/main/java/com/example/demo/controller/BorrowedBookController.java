package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.BorrowedBookDto;
import com.example.demo.dto.BorrowedBooksPaginatedDto;
import com.example.demo.dto.UpdateBorrowedBookDto;
import com.example.demo.model.BorrowStatus;
import com.example.demo.service.BorrowedBookService;
import com.example.demo.service.UserService;
import com.example.demo.validation.ValidSortByColumn;
import com.example.demo.validation.ValidSortDirection;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.SpringSecurityConfig.ADMIN;
import static com.example.demo.config.SpringSecurityConfig.USER;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = DemoApplication.PATH_V1 + "/borrowed-books")
@AllArgsConstructor
@Validated
public class BorrowedBookController {

    private final BorrowedBookService borrowedBookService;
    private final UserService userService;

    @RolesAllowed({USER, ADMIN})
    @GetMapping
    public BorrowedBooksPaginatedDto getBorrowedBooks(
            @RequestParam(defaultValue = "borrowedFrom")
            @Valid @ValidSortByColumn(allowedFields = {"borrowedId", "borrowedFrom", "returnDate"}) String sortBy,
            @RequestParam(defaultValue = "desc") @Valid @ValidSortDirection String direction,
            @RequestParam(defaultValue = "0") @Valid int pageNumber,
            @RequestParam(defaultValue = "10") @Max(15) @Valid int pageSize,
            @RequestParam(required = false) BorrowStatus status) {

        return borrowedBookService.getBorrowedBooks(sortBy, direction, pageNumber, pageSize, status);
    }

    @RolesAllowed({USER, ADMIN})
    @PatchMapping("/{borrowedId}")
    public BorrowedBookDto editBorrowedBook(@PathVariable Integer borrowedId,
                                            @RequestBody UpdateBorrowedBookDto updateBorrowedBookDto) {

        return borrowedBookService.editBorrowedBook(borrowedId, updateBorrowedBookDto);
    }

    @RolesAllowed({USER, ADMIN})
    @PostMapping("/{borrowedId}/return")
    public BorrowedBookDto returnBorrowedBook(@PathVariable Integer borrowedId) {
        String email = userService.getAuthenticatedUserEmail();
        return borrowedBookService.returnBorrowedBook(borrowedId, email);
    }
}
