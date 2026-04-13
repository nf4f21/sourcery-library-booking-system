package com.example.demo.controller.util;

import com.example.demo.dto.NewBookCopyDto;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class BookCopyApiTestClient {
    public static ValidatableResponse createNewBookCopy(NewBookCopyDto newCopy) {
        return given().contentType(ContentType.JSON)
                .when()
                .body(newCopy)
                .post("/api/v1/book-copies")
                .then();
    }

    public static ValidatableResponse deleteBookCopy(Integer bookCopyId) {
        return given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/book-copies/{bookCopyId}", bookCopyId)
                .then();
    }
}
