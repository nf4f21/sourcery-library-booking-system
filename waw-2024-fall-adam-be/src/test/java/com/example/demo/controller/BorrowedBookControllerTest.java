package com.example.demo.controller;

import com.example.demo.controller.base.BaseAuthenticatedControllerTest;
import com.example.demo.controller.util.BookApiTestClient;
import com.example.demo.controller.util.BookTestDataFactory;
import com.example.demo.controller.util.BorrowedBookApiTestClient;
import com.example.demo.controller.util.OfficeApiTestClient;
import com.example.demo.dto.BorrowedBookDto;
import com.example.demo.dto.NewBorrowedBookDto;
import com.example.demo.dto.UpdateBorrowedBookDto;
import com.example.demo.model.BorrowStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.example.demo.constants.BorrowedBookConstants.MAX_EXTENSIONS_COUNT;
import static com.example.demo.constants.BorrowedBookConstants.MAX_EXTENSION_LENGTH_DAYS;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DirtiesContext
class BorrowedBookControllerTest extends BaseAuthenticatedControllerTest {

    List<BorrowedBookDto> borrowBooks(int borrowedBooksCount) throws JSONException {
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, borrowedBooksCount));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        var createdBorrowedBooks = new ArrayList<BorrowedBookDto>();
        for (var i = 0; i < borrowedBooksCount; i++) {

            LocalDate returnDate = LocalDate.now().plusDays(i);
            NewBorrowedBookDto newBorrowedBookDto = new NewBorrowedBookDto(officeId, returnDate);

            var response = BookApiTestClient.createBorrowedBook(newBookId, newBorrowedBookDto);
            response
                    .statusCode(HttpStatus.OK.value())
                    .body("userId", equalTo(currentUser().userId()))
                    .body("title", equalTo(bookData.get("title")))
                    .body("author", equalTo(bookData.get("author")))
                    .body("coverImage", not(emptyArray()))
                    .body("officeName", equalTo("Kaunas"))
                    .body("status", equalTo("BORROWED"))
                    .body("borrowedFrom", equalTo(LocalDate.now().toString()))
                    .body("returnDate", equalTo(returnDate.toString()));
            var borrowedBook = new BorrowedBookDto(
                    response.extract().path("borrowedId"),
                    response.extract().path("userId"),
                    response.extract().path("bookCopyId"),
                    response.extract().path("title"),
                    response.extract().path("author"),
                    Base64.getDecoder().decode((String) response.extract().path("coverImage")),
                    response.extract().path("officeName"),
                    BorrowStatus.valueOf(response.extract().path("status")),
                    LocalDate.parse(response.extract().path("borrowedFrom")),
                    LocalDate.parse(response.extract().path("returnDate")));
            createdBorrowedBooks.add(borrowedBook);
        }
        return createdBorrowedBooks;
    }

    @Test
    void shouldRetrieveBorrowedBooksByUserSuccessfully() throws JSONException {

        String defaultValueSortBy = "borrowedFrom";
        String defaultValueDirection = "desc";
        int pageNumber = 0;
        int pageSize = 10;
        BorrowStatus status = null;

        int borrowedBooksCount = 2;
        var createdBorrowedBooks = borrowBooks(borrowedBooksCount);

        BorrowedBookApiTestClient.getBorrowedBooksByUser(defaultValueSortBy, defaultValueDirection, pageNumber, pageSize, status)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(borrowedBooksCount))
                .body("borrowedBooksCount", equalTo(borrowedBooksCount))
                .appendRootPath("borrowedBooks")
                .body("[0].borrowedId", equalTo(createdBorrowedBooks.get(0).borrowedId()))
                .body("[0].userId", equalTo(createdBorrowedBooks.get(0).userId()))
                .body("[0].bookCopyId", equalTo(createdBorrowedBooks.get(0).bookCopyId()))
                .body("[0].title", equalTo(createdBorrowedBooks.get(0).title()))
                .body("[0].author", equalTo(createdBorrowedBooks.get(0).author()))
                .body("[0].coverImage", not(emptyArray()))
                .body("[0].officeName", equalTo(createdBorrowedBooks.get(0).officeName()))
                .body("[0].status", equalTo(createdBorrowedBooks.get(0).status().toString()))
                .body("[0].borrowedFrom", equalTo(createdBorrowedBooks.get(0).borrowedFrom().toString()))
                .body("[0].returnDate", equalTo(createdBorrowedBooks.get(0).returnDate().toString()))
                .body("[1].borrowedId", equalTo(createdBorrowedBooks.get(1).borrowedId()))
                .body("[1].userId", equalTo(createdBorrowedBooks.get(1).userId()))
                .body("[1].bookCopyId", equalTo(createdBorrowedBooks.get(1).bookCopyId()))
                .body("[1].title", equalTo(createdBorrowedBooks.get(1).title()))
                .body("[1].author", equalTo(createdBorrowedBooks.get(1).author()))
                .body("[1].coverImage", not(emptyArray()))
                .body("[1].officeName", equalTo(createdBorrowedBooks.get(1).officeName()))
                .body("[1].status", equalTo(createdBorrowedBooks.get(1).status().toString()))
                .body("[1].borrowedFrom", equalTo(createdBorrowedBooks.get(1).borrowedFrom().toString()))
                .body("[1].returnDate", equalTo(createdBorrowedBooks.get(1).returnDate().toString()));
    }

    @Test
    void shouldRetrieveSortedByBorrowedFromAscBorrowedBooksByUserSuccessfully() throws JSONException {
        String sortBy = "borrowedFrom";
        String direction = "asc";
        int pageNumber = 0;
        int pageSize = 10;
        BorrowStatus status = null;

        int borrowedBooksCount = 2;
        borrowBooks(borrowedBooksCount);

        BorrowedBookApiTestClient.getBorrowedBooksByUser(sortBy, direction, pageNumber, pageSize, status)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(borrowedBooksCount));
    }

    @Test
    void shouldRetrieveSortedByReturnDateAscBorrowedBooksByUserSuccessfully() throws JSONException {
        String sortBy = "returnDate";
        String direction = "asc";
        int pageNumber = 0;
        int pageSize = 10;
        BorrowStatus status = null;

        int borrowedBooksCount = 2;
        borrowBooks(borrowedBooksCount);

        BorrowedBookApiTestClient.getBorrowedBooksByUser(sortBy, direction, pageNumber, pageSize, status)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(2))
                .appendRootPath("borrowedBooks")
                .body("[0].returnDate", equalTo(LocalDate.now().toString()))
                .body("[1].returnDate", equalTo(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    void shouldRetrieveSortedByrReturnDateDescBorrowedBooksByUserSuccessfully() throws JSONException {
        String sortBy = "returnDate";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;
        BorrowStatus status = null;

        int borrowedBooksCount = 2;
        borrowBooks(borrowedBooksCount);

        BorrowedBookApiTestClient.getBorrowedBooksByUser(sortBy, direction, pageNumber, pageSize, status)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(2))
                .appendRootPath("borrowedBooks")
                .body("[0].returnDate", equalTo(LocalDate.now().plusDays(1).toString()))
                .body("[1].returnDate", equalTo(LocalDate.now().toString()));
    }

    @Test
    void shouldReturnBorrowedBookByIdSuccessfully() throws JSONException {
        int borrowedBooksCount = 2;
        var createdBorrowedBooks = borrowBooks(borrowedBooksCount);
        BorrowedBookApiTestClient.returnBorrowedBook(createdBorrowedBooks.getFirst().borrowedId())
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("RETURNED"));
    }

    @Test
    void shouldReturnBadRequestForInvalidSortByValue() throws JSONException {
        String sortBy = "notCorrectValue";
        String direction = "asc";
        int pageNumber = 0;
        int pageSize = 10;
        BorrowStatus status = null;

        int borrowedBooksCount = 2;
        borrowBooks(borrowedBooksCount);

        BorrowedBookApiTestClient.getBorrowedBooksByUser(sortBy, direction, pageNumber, pageSize, status)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldExtendReturnDateWhenAllowed() throws JSONException {
        // Arrange
        int borrowedBooksCount = 1;
        var createdBorrowedBooks = borrowBooks(borrowedBooksCount);
        UpdateBorrowedBookDto updateBorrowedBookDto = new UpdateBorrowedBookDto(LocalDate.now().plusDays(3));

        // Act
        BorrowedBookApiTestClient.editBorrowedBook(createdBorrowedBooks.getFirst().borrowedId(), updateBorrowedBookDto)
                .statusCode(HttpStatus.OK.value())
                .body("returnDate", equalTo(updateBorrowedBookDto.returnDate().toString()));
    }

    @Test
    void shouldNotExtendReturnDateWhenTooLong() throws JSONException {
        // Arrange
        int borrowedBooksCount = 1;
        int extensionLength = MAX_EXTENSION_LENGTH_DAYS + 1;
        var createdBorrowedBooks = borrowBooks(borrowedBooksCount);
        UpdateBorrowedBookDto updateBorrowedBookDto = new UpdateBorrowedBookDto(
                LocalDate.now().plusDays(extensionLength));

        // Act
        BorrowedBookApiTestClient.editBorrowedBook(createdBorrowedBooks.getFirst().borrowedId(), updateBorrowedBookDto)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldNotAllowTooManyReturnDateExtensions() throws JSONException {
        // Arrange
        int borrowedBooksCount = 1;
        var createdBorrowedBooks = borrowBooks(borrowedBooksCount);
        UpdateBorrowedBookDto updateBorrowedBookDto = new UpdateBorrowedBookDto(LocalDate.now().plusDays(3));

        // Act
        for (int i = 0; i < MAX_EXTENSIONS_COUNT; i++) {
            BorrowedBookApiTestClient.editBorrowedBook(createdBorrowedBooks.getFirst().borrowedId(),
                            updateBorrowedBookDto)
                    .statusCode(HttpStatus.OK.value());
        }
        BorrowedBookApiTestClient.editBorrowedBook(createdBorrowedBooks.getFirst().borrowedId(), updateBorrowedBookDto)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}