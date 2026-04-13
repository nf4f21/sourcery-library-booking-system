package com.example.demo.controller.util;

import com.example.demo.dto.NewBorrowedBookDto;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import java.io.File;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * A utility class for sending requests in tests
 */
public class BookApiTestClient {
    public static ValidatableResponse addBook(Map<String, String> bookData,
                                              File coverImageFile) {
        return given().contentType(ContentType.MULTIPART)
                .multiPart(new MultiPartSpecBuilder(coverImageFile)
                        .fileName("book_cover.png")
                        .controlName("coverImage")
                        .mimeType("image/png")
                        .build())
                .formParams(bookData)
                .when()
                .post("/api/v1/books")
                .then()
                .log().ifValidationFails();
    }

    public static ValidatableResponse editBook(Integer bookId,
                                               Map<String, String> bookData,
                                               File coverImageFile) {
        return given().contentType(ContentType.MULTIPART)
                .multiPart(new MultiPartSpecBuilder(coverImageFile)
                        .fileName("book_cover.png")
                        .controlName("coverImage")
                        .mimeType("image/png")
                        .build())
                .formParams(bookData)
                .when()
                .put("/api/v1/books/%d".formatted(bookId))
                .then()
                .log().ifValidationFails();
    }

    public static ValidatableResponse getBookById(Integer bookId) {
        return given()
                .when()
                .get("/api/v1/books/{bookId}", bookId)
                .then();
    }

    public static ValidatableResponse addBookWithInvalidFileType(Map<String, String> bookData, File coverImageFile) {
        return given().contentType(ContentType.MULTIPART)
                .multiPart(new MultiPartSpecBuilder(coverImageFile)
                        .fileName("book_cover.txt")
                        .controlName("coverImage")
                        .mimeType("text/plain")  // Invalid type for testing
                        .build())
                .formParams(bookData)
                .when()
                .post("/api/v1/books")
                .then();
    }

    public static ValidatableResponse createBorrowedBook(Integer bookId, NewBorrowedBookDto newBorrowedBookDto) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("bookId", bookId)
                .body(newBorrowedBookDto)
                .when()
                .post("/api/v1/books/{bookId}/borrow")
                .then();
    }


    public static ValidatableResponse getActiveReservationsForBook(Integer bookId) {
        return given()
                .when()
                .get("/api/v1/books/{bookId}/active-reservations", bookId)
                .then();
    }

    public static ValidatableResponse getBookAvailability(Integer bookId) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("bookId", bookId)
                .when()
                .get("/api/v1/books/{bookId}/availability")
                .then()
                .log().ifValidationFails();
    }

    public static ValidatableResponse getPagedBooksWithFilters(String filters) {
        return given()
                .queryParam("filter", filters)
                .when()
                .get("/api/v1/books")
                .then()
                .log().ifValidationFails();
    }
}
