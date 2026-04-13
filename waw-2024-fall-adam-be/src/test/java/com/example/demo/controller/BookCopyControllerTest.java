package com.example.demo.controller;

import com.example.demo.controller.base.BaseAuthenticatedControllerTest;
import com.example.demo.controller.util.BookApiTestClient;
import com.example.demo.controller.util.BookCopyApiTestClient;
import com.example.demo.controller.util.BookTestDataFactory;
import com.example.demo.controller.util.OfficeApiTestClient;
import com.example.demo.dto.NewBookCopyDto;
import com.example.demo.dto.NewBorrowedBookDto;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("local")
@DirtiesContext
public class BookCopyControllerTest extends BaseAuthenticatedControllerTest {

    private Integer createExampleBook() throws JSONException {
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        bookData.put("newBookCopies", "[]");

        return BookApiTestClient.addBook(bookData,
                        new File("./src/test/resources/assets/book_cover.png"))
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");
    }

    @Test
    void shouldAdd2BookCopies() throws JSONException {
        int officeId = OfficeApiTestClient.getOfficeIdAt(0);
        int bookId = createExampleBook();
        var copyCount = 2;
        var newCopy = new NewBookCopyDto(officeId, copyCount, bookId);

        BookCopyApiTestClient.createNewBookCopy(newCopy)
                .statusCode(HttpStatus.OK.value())
                .body("[0].bookCopyId", notNullValue())
                .body("[0].officeId", equalTo(officeId))
                .body("[0].isAvailable", equalTo(true))
                .body("[0].bookId", equalTo(bookId))
                .body("[1].bookCopyId", notNullValue())
                .body("[1].officeId", equalTo(officeId))
                .body("[1].isAvailable", equalTo(true))
                .body("[1].bookId", equalTo(bookId));
    }

    @Test
    void shouldDeleteAvailableBookCopy() throws JSONException {
        // arrange
        int officeId = OfficeApiTestClient.getOfficeIdAt(0);
        int bookId = createExampleBook();
        int copyCount = 1;

        var newCopy = new NewBookCopyDto(officeId, copyCount, bookId);

        Integer bookCopyId = BookCopyApiTestClient.createNewBookCopy(newCopy)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].bookCopyId");

        // act & assert
        BookCopyApiTestClient.deleteBookCopy(bookCopyId)
                .statusCode(HttpStatus.OK.value());
        BookApiTestClient.getBookById(bookId)
                .statusCode(HttpStatus.OK.value())
                .body("bookCopies.size()", equalTo(0));
    }

    @Test
    void shouldNotDeleteNonExistentBookCopy() {
        Integer bookCopyId = 999999999;
        BookCopyApiTestClient.deleteBookCopy(bookCopyId)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldNotDeleteUnavailableBookCopy() throws JSONException {
        // arrange
        int officeId = OfficeApiTestClient.getOfficeIdAt(0);
        int bookId = createExampleBook();
        int copyCount = 1;

        var newCopy = new NewBookCopyDto(officeId, copyCount, bookId);

        Integer bookCopyId = BookCopyApiTestClient.createNewBookCopy(newCopy)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].bookCopyId");

        // borrow the newly created book
        NewBorrowedBookDto borrowData = new NewBorrowedBookDto(officeId, LocalDate.now().plusDays(1));
        BookApiTestClient.createBorrowedBook(bookId, borrowData);

        // act & assert
        BookCopyApiTestClient.deleteBookCopy(bookCopyId)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
