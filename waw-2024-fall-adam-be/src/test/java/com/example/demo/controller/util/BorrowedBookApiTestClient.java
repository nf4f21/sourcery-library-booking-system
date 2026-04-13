package com.example.demo.controller.util;

import com.example.demo.dto.UpdateBorrowedBookDto;
import com.example.demo.model.BorrowStatus;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class BorrowedBookApiTestClient {
    public static ValidatableResponse getBorrowedBooksByUser(String sortBy, String direction, int pageNumber,
                                                             int pageSize, BorrowStatus status) {
        return given()
                .contentType(ContentType.JSON)
                .param("sortBy", sortBy)
                .param("direction", direction)
                .param("pageNumber", pageNumber)
                .param("pageSize", pageSize)
                .param("status", status)
                .when()
                .get("/api/v1/borrowed-books")
                .then();
    }

    public static ValidatableResponse returnBorrowedBook(Integer borrowedId) {
        return given()
                .when()
                .pathParam("borrowedId", borrowedId)
                .post("/api/v1/borrowed-books/{borrowedId}/return")
                .then();
    }

    public static ValidatableResponse editBorrowedBook(Integer borrowedId, UpdateBorrowedBookDto updateBorrowedBookDto) {
        return given()
                .body(updateBorrowedBookDto)
                .when()
                .patch("/api/v1/borrowed-books/{borrowedId}", borrowedId)
                .then();
    }
}