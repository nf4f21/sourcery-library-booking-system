package com.example.demo.controller.util;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OfficeApiTestClient {
    public static ValidatableResponse getOffices() {
        return given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/offices")
                .then();
    }

    public static Integer getOfficeIdAt(int index) {
        return getOffices()
                .extract()
                .path("[%d].officeId".formatted(index));
    }

    public static ValidatableResponse getBookCopyAvailabilityInEachOfficeInfo(Integer bookId) {
        return given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/offices/book/" + bookId)
                .then();
    }
}
