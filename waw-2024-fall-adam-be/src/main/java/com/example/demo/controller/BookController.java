package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.*;
import com.example.demo.exception.InvalidFileException;
import com.example.demo.service.BookService;
import com.example.demo.service.BorrowedBookService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.example.demo.config.SpringSecurityConfig.ADMIN;
import static com.example.demo.config.SpringSecurityConfig.USER;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = DemoApplication.PATH_V1 + "/books")
@AllArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;
    private final ObjectMapper objectMapper;
    private final BorrowedBookService borrowedBookService;
    private final UserService userService;

    @RolesAllowed(ADMIN)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BookDto createNewBook(MultipartHttpServletRequest request) {
        NewBookDto newBook = extractNewBookDto(request);
        return bookService.saveNewBook(newBook);
    }

    @RolesAllowed(ADMIN)
    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BookDto editBookDetails(@PathVariable Integer bookId,
                                   MultipartHttpServletRequest request) {
        NewBookDto updatedBookDetails = extractNewBookDto(request);

        return bookService.editBookDetails(bookId, updatedBookDetails);
    }

    @RolesAllowed({USER, ADMIN})
    @GetMapping()
    public BookPagedDto getBooksWithFilters(@ParameterObject @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
                                            // @Parameter - for better Swagger documentation of the filter parameter
                                            @Parameter(description = "JSON string containing book filters: {isAvailableFilter: boolean, officeIdFilter: List of Integer, categoryFilter: List of String}",
                                                    schema = @Schema(type = "string", example = "{\"isAvailableFilter\":true,\"officeIdFilter\":[1,2,3],\"categoryFilter\":[\"Fantasy\",\"Development\"]}"))
                                                @RequestParam(name = "filter", required = false, defaultValue = "{}") String bookFilters) {
        return bookService.getPagedBooksWithFilters(pageable, bookFilters);
    }

    @RolesAllowed({USER, ADMIN})
    @GetMapping("/{bookId}")
    public BookDto getBookById(@PathVariable Integer bookId) {
        return bookService.getBookById(bookId);
    }

    private NewBookDto extractNewBookDto(MultipartHttpServletRequest request) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String description = request.getParameter("description");
        String format = request.getParameter("format");
        Integer numberOfPages = Integer.parseInt(request.getParameter("numberOfPages"));
        LocalDate publicationDate = LocalDate.parse(request.getParameter("publicationDate"));
        String publisher = request.getParameter("publisher");
        String isbn = request.getParameter("isbn");
        String editionLanguage = request.getParameter("editionLanguage");
        String series = request.getParameter("series");
        String category = request.getParameter("category");
        var coverImage = request.getFile("coverImage");
        // Here, we collect the book copies as a JSON string because that's how it will be passed from the frontend
        String bookCopiesJson = request.getParameter("newBookCopies");

        List<@Valid NewBookCopyDto> bookCopies;


        // Parse the bookCopiesJson into a list of NewBookCopyDto objects
        try {
            bookCopies = objectMapper.readValue(
                    bookCopiesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewBookCopyDto.class)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse bookCopies JSON", e);
        }

        // Make sure NewBookDto has a matching constructor
        return new NewBookDto(
                coverImage, title, author, description, format, numberOfPages,
                publicationDate, publisher, isbn, editionLanguage, series, category, bookCopies
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidFileException(InvalidFileException ex) {
        return Map.of("error", ex.getMessage());
    }

    @RolesAllowed({USER, ADMIN})
    @PostMapping("/{bookId}/borrow")
    public BorrowedBookDto createBookBorrowing(@RequestBody NewBorrowedBookDto newBorrowing, @PathVariable Integer bookId) {
        String email = userService.getAuthenticatedUserEmail();
        return borrowedBookService.saveNewBookBorrowing(newBorrowing, bookId, email);
    }

    @RolesAllowed({ADMIN, USER})
    @GetMapping("/{bookId}/active-reservations")
    public List<ActiveBookReservationsDto> getActiveBookReservations(@PathVariable Integer bookId) {
        return borrowedBookService.getActiveReservationsForBook(bookId);
    }

    @RolesAllowed({USER, ADMIN})
    @GetMapping("/{bookId}/availability")
    public boolean getBookAvailability(@PathVariable Integer bookId) {
        return bookService.getBookAvailability(bookId);
    }
}
