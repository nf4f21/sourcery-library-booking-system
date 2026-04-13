package com.example.demo.controller;

import com.example.demo.controller.base.BaseAuthenticatedControllerTest;
import com.example.demo.controller.util.BookTestDataFactory;
import com.example.demo.controller.util.OfficeApiTestClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.Map;

import static com.example.demo.controller.util.BookApiTestClient.addBook;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("local")
public class OfficeControllerTest extends BaseAuthenticatedControllerTest {

    @Test
    void shouldRetrieveOfficesSuccessfully() {
        // Act & Assert
        OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(5))
                .body("[0].officeId", equalTo(1))
                .body("[0].name", equalTo("Kaunas"))
                .body("[1].officeId", equalTo(2))
                .body("[1].name", equalTo("Vilnius"))
                .body("[2].officeId", equalTo(3))
                .body("[2].name", equalTo("London"))
                .body("[3].officeId", equalTo(4))
                .body("[3].name", equalTo("Chicago"))
                .body("[4].officeId", equalTo(5))
                .body("[4].name", equalTo("Toronto"));
    }

    @Test
    void shouldRetrieveDetailedOfficesInfoSuccessfully() throws JSONException {
        // Arrange
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");

        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        JSONArray bookCopies = BookTestDataFactory.createBookCopies(Map.of(
                3, 4 // {officeId: 3, copyCount: 4}
        ));

        bookData.put("newBookCopies", bookCopies.toString());

        Integer bookId = addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        // Act & Assert
        OfficeApiTestClient.getBookCopyAvailabilityInEachOfficeInfo(bookId)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(5))
                .body("[0].basicOffice.officeId", equalTo(3))
                .body("[0].basicOffice.name", equalTo("London"))
                .body("[0].copiesAvailable", equalTo(4))
                .body("[0].address", equalTo("Liverpool St 34-37, EC2M 7PP London, United Kingdom"));

    }

    @Test
    void shouldReturnErrorForNonExistentBookId() {
        // Arrange
        Integer nonExistentBookId = 9999;
        // Act & assert
        OfficeApiTestClient.getBookCopyAvailabilityInEachOfficeInfo(nonExistentBookId)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
