package com.example.demo.controller;

import com.example.demo.controller.base.BaseAuthenticatedControllerTest;
import com.example.demo.controller.util.BookApiTestClient;
import com.example.demo.controller.util.BookTestDataFactory;
import com.example.demo.controller.util.OfficeApiTestClient;
import com.example.demo.dto.BasicOfficeDto;
import com.example.demo.dto.NewBorrowedBookDto;
import com.example.demo.model.BookCopyEntity;
import com.example.demo.model.BookEntity;
import com.example.demo.model.OfficeEntity;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OfficeRepository;
import com.example.demo.service.OfficeService;
import io.restassured.response.ValidatableResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.controller.util.BookApiTestClient.addBook;
import static com.example.demo.controller.util.BookApiTestClient.editBook;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("local")
@DirtiesContext
public class BookControllerTest extends BaseAuthenticatedControllerTest {

    private List<Map<String, Object>> offices;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private OfficeService officeService;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        // If needed, fetch offices before running tests
        offices = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("$");

        if (offices == null || offices.isEmpty()) {
            throw new IllegalStateException(
                    "No offices found. Ensure office endpoint is working and offices are available.");
        }
    }

    static void assertImageCoverEqual(Path pathToExpectedCover, String actualCoverImage) throws IOException {
        byte[] actualCoverImageBytes = Base64.getDecoder().decode(actualCoverImage);
        assertArrayEquals(
                Files.readAllBytes(pathToExpectedCover),
                actualCoverImageBytes,
                "The returned cover image should match the uploaded one.");
    }

    @Test
    void shouldAddNewBook() throws IOException, JSONException {
        // Arrange
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        // Act & Assert
        // Use office IDs from the retrieved list for the book data, adding multiple
        // office copies
        Map<Integer, Integer> bookCopiesData = new HashMap<>();
        int copiesPerOffice = 2;
        int officesLimit = Math.min(offices.size(), 3);  // Limit the number of offices for the test
        for (int i = 0; i < officesLimit; i++) {
            Integer officeId = Integer.valueOf(offices.get(i).get("officeId").toString());
            bookCopiesData.put(officeId, copiesPerOffice);
        }
        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(bookCopiesData);
        bookData.put("newBookCopies", newBookCopies.toString());

        ValidatableResponse response = addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .body("bookId", notNullValue())
                .body("coverImage", notNullValue())
                .body("title", equalTo(bookData.get("title")))
                .body("author", equalTo(bookData.get("author")))
                .body("description", equalTo(bookData.get("description")))
                .body("format", equalTo(bookData.get("format")))
                .body("numberOfPages", equalTo(Integer.parseInt(bookData.get("numberOfPages"))))
                .body("publicationDate", equalTo(bookData.get("publicationDate")))
                .body("publisher", equalTo(bookData.get("publisher")))
                .body("isbn", equalTo(bookData.get("isbn")))
                .body("editionLanguage", equalTo(bookData.get("editionLanguage")))
                .body("series", equalTo(bookData.get("series")))
                .body("category", equalTo(bookData.get("category")));

        for (int officeIndex = 0; officeIndex < officesLimit; officeIndex++) {
            for (int bookCopyRelativeIdx = 0; bookCopyRelativeIdx < copiesPerOffice; bookCopyRelativeIdx++) {
                String expectedOfficeId = offices.get(officeIndex).get("officeId").toString();
                int bookCopyAbsoluteIdx = officeIndex * copiesPerOffice + bookCopyRelativeIdx;
                response
                        .body("bookCopies[" + bookCopyAbsoluteIdx + "].officeId", equalTo(Integer.parseInt(expectedOfficeId)))
                        .body("bookCopies[" + bookCopyAbsoluteIdx + "].isAvailable", equalTo(true));
            }
        }

        // validate cover image
        assertImageCoverEqual(
                coverImageFile.toPath(),
                response.extract().path("coverImage"));

    }

    @Test
    void shouldNotEditNonExistingBook() throws JSONException {
        // Arrange
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        // Act & assert
        editBook(100000, bookData, coverImageFile)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldEditExistingBook() throws IOException, JSONException {
        // Arrange
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        // Add a book to edit later
        Integer newBookId = addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        // Act & Assert
        // Get updated book data
        File newCoverImageFile = new File("./src/test/resources/assets/book_cover_edited.png");
        Map<String, String> newBookData = BookTestDataFactory.getAlternativeBookFormData();
        ValidatableResponse response = editBook(newBookId, newBookData, newCoverImageFile)
                .statusCode(HttpStatus.OK.value())
                .body("bookId", equalTo(newBookId))
                .body("coverImage", notNullValue())
                .body("title", equalTo(newBookData.get("title")))
                .body("author", equalTo(newBookData.get("author")))
                .body("description", equalTo(newBookData.get("description")))
                .body("format", equalTo(newBookData.get("format")))
                .body("numberOfPages", equalTo(Integer.parseInt(newBookData.get("numberOfPages"))))
                .body("publicationDate", equalTo(newBookData.get("publicationDate")))
                .body("publisher", equalTo(newBookData.get("publisher")))
                .body("isbn", equalTo(newBookData.get("isbn")))
                .body("editionLanguage", equalTo(newBookData.get("editionLanguage")))
                .body("series", equalTo(newBookData.get("series")))
                .body("category", equalTo(newBookData.get("category")));

        // Validate that the cover image was updated
        assertImageCoverEqual(
                newCoverImageFile.toPath(),
                response.extract().path("coverImage"));
    }

    @Test
    void saveNewBook_shouldHandleZeroCopies() throws JSONException {
        // Arrange
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(1, 0));
        bookData.put("newBookCopies", newBookCopies.toString());
        // Act & Assert
        BookApiTestClient.addBook(bookData, new File("./src/test/resources/assets/book_cover.png"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void saveNewBook_shouldThrowExceptionForInvalidOfficeId() throws JSONException {
        // Arrange
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(9999, 3));
        bookData.put("newBookCopies", newBookCopies.toString());

        // Act & Assert
        BookApiTestClient.addBook(bookData, new File("./src/test/resources/assets/book_cover.png"))
                .statusCode(HttpStatus.BAD_REQUEST.value())  // Expecting 400 for invalid input
                .body("error", equalTo("Office with ID 9999 not found"));  // Check the error message
    }

    @Test
    void saveNewBook_shouldThrowExceptionForInvalidFileType() throws JSONException {
        // Arrange
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        File invalidFile = new File("./src/test/resources/assets/book_cover.txt");

        // Act & Assert
        BookApiTestClient.addBookWithInvalidFileType(bookData, invalidFile)
                .statusCode(HttpStatus.BAD_REQUEST.value()) // Expecting 400 for invalid input
                .body("error", equalTo("The file has not correct type, only acceptable types are jpg, png, jpeg"));
    }

    @Test
    void saveNewBook_shouldThrowExceptionForNegativeOfficeId() throws JSONException {
        // Arrange
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(-1, 2));
        bookData.put("newBookCopies", newBookCopies.toString());

        // Act & Assert
        BookApiTestClient.addBook(bookData, new File("./src/test/resources/assets/book_cover.png"))
                .statusCode(HttpStatus.BAD_REQUEST.value()) // Expecting 400 for invalid input
                .body("error", equalTo("Office ID cannot be negative: -1"));
    }

    @Test
    void shouldCreateBorrowedBook() throws JSONException {
        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, 1));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        LocalDate returnDate = LocalDate.of(2024, 11, 13);
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
    }

    @Test
    void shouldReturn404ForNotExistingBookCopy() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");

        var bookData = BookTestDataFactory.getSampleBookFormData();
        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(1, 0));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].officeId");

        LocalDate returnDate = LocalDate.of(2024, 11, 13);
        NewBorrowedBookDto newBorrowedBookDto = new NewBorrowedBookDto(officeId, returnDate);
        var response = BookApiTestClient.createBorrowedBook(newBookId, newBorrowedBookDto);
        response
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturn404ForAlreadyBorrowedBookCopy() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[2].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, 1));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        LocalDate returnDate = LocalDate.of(2024, 11, 13);
        NewBorrowedBookDto newBorrowedBookDto = new NewBorrowedBookDto(officeId, returnDate);

        BookApiTestClient.createBorrowedBook(newBookId, newBorrowedBookDto)
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(currentUser().userId()))
                .body("title", equalTo(bookData.get("title")))
                .body("author", equalTo(bookData.get("author")))
                .body("coverImage", not(emptyArray()))
                .body("officeName", equalTo("London"))
                .body("status", equalTo("BORROWED"))
                .body("borrowedFrom", equalTo(LocalDate.now().toString()))
                .body("returnDate", equalTo(returnDate.toString()));

        BookApiTestClient.createBorrowedBook(newBookId, newBorrowedBookDto)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnTrueForAvailableBook() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[2].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, 1));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        BookApiTestClient.getBookAvailability(newBookId)
                .statusCode(HttpStatus.OK.value())
                .body(equalTo(Boolean.toString(true)));
    }

    @Test
    void shouldReturnFalseForNotAvailableBook() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[2].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, 0));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer newBookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        BookApiTestClient.getBookAvailability(newBookId)
                .statusCode(HttpStatus.OK.value())
                .body(equalTo(Boolean.toString(false)));
    }

    @Test
    void shouldReturn2Books() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();
        Map<String, String> bookData1 = BookTestDataFactory.getAlternativeBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[2].officeId");

        JSONArray newBookCopies1 = BookTestDataFactory.createBookCopies(Map.of(officeId, 1));
        bookData.put("newBookCopies", newBookCopies1.toString());

        int bookId1 = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        JSONArray newBookCopies2 = BookTestDataFactory.createBookCopies(Map.of(officeId, 1));
        bookData.put("newBookCopies", newBookCopies2.toString());

        int bookId2 = BookApiTestClient.addBook(bookData1, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        LocalDate returnDate = LocalDate.of(2024, 11, 13);
        NewBorrowedBookDto newBorrowedBookDto = new NewBorrowedBookDto(officeId, returnDate);

        BookApiTestClient.createBorrowedBook(bookId1, newBorrowedBookDto)
                .statusCode(HttpStatus.OK.value());

        BookApiTestClient.getPagedBooksWithFilters(null)
                .statusCode(HttpStatus.OK.value())
                .body("books[0].coverImage", notNullValue())
                .body("books[0].bookId", equalTo(bookId1))
                .body("books[0].title", equalTo(bookData.get("title")))
                .body("books[0].author", equalTo(bookData.get("author")))
                .body("books[0].isAvailable", equalTo(false))
                .body("books[1].coverImage", notNullValue())
                .body("books[1].bookId", equalTo(bookId2))
                .body("books[1].title", equalTo(bookData1.get("title")))
                .body("books[1].author", equalTo(bookData1.get("author")))
                .body("books[1].isAvailable", equalTo(true));
    }

    // TESTS FOR GET ENDPOINT WITH FILTERING ON MULTIPLE (STATUES, CATEGORIES, OFFICES)

    @Nested
    public class getPagedBooksWithFilters {

        @BeforeEach
        void setUp() throws IOException {
            File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
            byte[] coverImage = Files.readAllBytes(coverImageFile.toPath());

            // Save books
            BookEntity bookOne = new BookEntity(null, coverImage, "Sample Title 1", "Sample Author 1",
                    "Sample Description 1", "Hardcover", 300, LocalDate.of(2020, 1, 1),
                    "Sample Publisher 1", "111-1-11-111111-1", "English", "Sample Series 1", "Fantasy", null);
            bookRepository.save(bookOne);

            BookEntity bookTwo = new BookEntity(null, coverImage, "Alternative Title 2", "Alternative Author 2",
                    "Alternative Description 2", "Paperback", 250, LocalDate.of(2021, 2, 15),
                    "Alternative Publisher 2", "222-2-22-222222-2", "English", "Alternative Series 2", "Dystopia", null);
            bookRepository.save(bookTwo);

            BookEntity bookThree = new BookEntity(null, coverImage, "Sample Title 3", "Sample Author 3",
                    "Sample Description 3", "Hardcover", 400, LocalDate.of(2019, 3, 10),
                    "Sample Publisher 3", "333-3-33-333333-3", "English", "Sample Series 3", "Fantasy", null);
            bookRepository.save(bookThree);

            BookEntity bookFour = new BookEntity(null, coverImage, "Alternative Title 4", "Alternative Author 4",
                    "Alternative Description 4", "Ebook", 200, LocalDate.of(2018, 4, 20),
                    "Alternative Publisher 4", "444-4-44-444444-4", "English", "Alternative Series 4", "Computer Science", null);
            bookRepository.save(bookFour);

            BookEntity bookFive = new BookEntity(null, coverImage, "Sample Title 5", "Sample Author 5",
                    "Sample Description 5", "Hardcover", 350, LocalDate.of(2017, 5, 25),
                    "Sample Publisher 5", "555-5-55-555555-5", "English", "Sample Series 5", "Computer Science", null);
            bookRepository.save(bookFive);

            BookEntity bookSix = new BookEntity(null, coverImage, "Alternative Title 6", "Alternative Author 6",
                    "Alternative Description 6", "Paperback", 300, LocalDate.of(2016, 6, 30),
                    "Alternative Publisher 6", "666-6-66-666666-6", "English", "Alternative Series 6", "Business", null);
            bookRepository.save(bookSix);

            BookEntity bookSeven = new BookEntity(null, coverImage, "Sample Title 7", "Sample Author 7",
                    "Sample Description 7", "Hardcover", 320, LocalDate.of(2015, 7, 5),
                    "Sample Publisher 7", "777-7-77-777777-7", "English", "Sample Series 7", "Business", null);
            bookRepository.save(bookSeven);

            BookEntity bookEight = new BookEntity(null, coverImage, "Alternative Title 8", "Alternative Author 8",
                    "Alternative Description 8", "Ebook", 275, LocalDate.of(2014, 8, 10),
                    "Alternative Publisher 8", "888-8-88-888888-8", "English", "Alternative Series 8", "Data Science", null);
            bookRepository.save(bookEight);

            BookEntity bookNine = new BookEntity(null, coverImage, "Sample Title 9", "Sample Author 9",
                    "Sample Description 9", "Paperback", 300, LocalDate.of(2013, 9, 15),
                    "Sample Publisher 9", "999-9-99-999999-9", "English", "Sample Series 9", "Design", null);
            bookRepository.save(bookNine);

            // Create Book Copies (books 1, 2, 3 are books without Copies)

            // Fetch office entities
            List<BasicOfficeDto> officesData = officeService.getOffices(0, 10);

            OfficeEntity officeOneEntity = officeRepository.findById(officesData.get(0).officeId()).orElseThrow();
            OfficeEntity officeTwoEntity = officeRepository.findById(officesData.get(1).officeId()).orElseThrow();
            OfficeEntity officeThreeEntity = officeRepository.findById(officesData.get(2).officeId()).orElseThrow();
            OfficeEntity officeFourEntity = officeRepository.findById(officesData.get(3).officeId()).orElseThrow();
            OfficeEntity officeFiveEntity = officeRepository.findById(officesData.get(4).officeId()).orElseThrow();

            // Save book copies
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookFour, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookFour, officeTwoEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeTwoEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookFour, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeFourEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeFourEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeFiveEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFour, officeFiveEntity));

            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeTwoEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeTwoEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeTwoEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookFive, officeFiveEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookFive, officeFiveEntity));

            bookCopyRepository.save(new BookCopyEntity(null, true, bookSix, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookSix, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookSix, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookSix, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, false, bookSix, officeOneEntity));

            bookCopyRepository.save(new BookCopyEntity(null, true, bookSeven, officeOneEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookSeven, officeOneEntity));

            bookCopyRepository.save(new BookCopyEntity(null, true, bookEight, officeOneEntity));

            bookCopyRepository.save(new BookCopyEntity(null, true, bookNine, officeThreeEntity));
            bookCopyRepository.save(new BookCopyEntity(null, true, bookNine, officeThreeEntity));
        }

        @Test
        void getPagedBooksWithFilters_WhenFilterIsIncorrectType_ThrowsError() {
            String filters = "not_json";

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void getPagedBooksWithFilters_WhenFilterParamIsMissing_ReturnsAllBooks() {
            BookApiTestClient.getPagedBooksWithFilters(null)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenAllFiltersAreMissing_ReturnsAllBooks() {
            String filters = "{}";

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsAvailableFilterIsTrue_ReturnsAvailableBooks() {
            String filters = """
                    {"isAvailableFilter":true}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(6))
                    .body("total", equalTo(6))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsAvailableFilterIsFalse_ReturnsUnavailableBooks() {
            String filters = """
                    {"isAvailableFilter":false}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(3))
                    .body("total", equalTo(3))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsAvailableFilterIsNull_ReturnsAllBooks() {
            String filters = """
                    {"isAvailableFilter":null}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsOfficeIdsFilterIsEmpty_ReturnsAllBooks() {
            String filters = """
                    {"officeIdFilter":[]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsOfficeIdsFilterHasOneValue_ReturnsBooksWithMatchingOfficeId() {
            String filters = """
                    {"officeIdFilter":[1]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsOfficeIdsFilterHasMultipleValues_ReturnsBooksWithMatchingOfficeIds() {
            String filters = """
                    {"officeIdFilter":[1,2,3]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsCategoryFilterIsEmpty_ReturnsAllBooks() {
            String filters = """
                    {"categoryFilter":[]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsCategoryFilterHasOneValue_ReturnsBooksWithMatchingCategory() {
            String filters = """
                    {"categoryFilter":["Fantasy"]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(2))
                    .body("total", equalTo(2))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenIsCategoryFilterHasMultipleValues_ReturnsBooksWithMatchingCategories() {
            String filters = """
                    {"categoryFilter":["Fantasy","Dystopia"]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(3))
                    .body("total", equalTo(3))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenAllFiltersAreNull_ReturnsAllBooks() {
            String filters = """
                    {"isAvailableFilter":null,"officeIdFilter":null,"categoryFilter":null}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenFiltersArePresentButEmpty_ReturnsAllBooks() {
            String filters = """
                    {"isAvailableFilter":null,"officeIdFilter":[],"categoryFilter":[]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenFiltersArePartiallyProvided_ReturnsBooksAccordingToProvidedFilters() {
            String filters = """
                    {"isAvailableFilter":true,"officeIdFilter":[1,2],"categoryFilter":["Computer Science"]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(1))
                    .body("total", equalTo(1))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenFiltersArePartiallyProvided_ReturnsBooksAccordingToProvidedOfficeAndStatusFilters() {
            String filters = """
                    {"isAvailableFilter":true,"officeIdFilter":[2]}
                    """;


            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(1))
                    .body("total", equalTo(1))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenFilterContainsInvalidOfficeId_ReturnsAllBooksWithIsAvailableStatusFalse() {
            String filters = """
                    {"officeIdFilter":[9999]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(9))
                    .body("books[0].isAvailable", equalTo(false))
                    .body("books[1].isAvailable", equalTo(false))
                    .body("books[2].isAvailable", equalTo(false))
                    .body("books[3].isAvailable", equalTo(false))
                    .body("books[4].isAvailable", equalTo(false))
                    .body("books[5].isAvailable", equalTo(false))
                    .body("books[6].isAvailable", equalTo(false))
                    .body("books[7].isAvailable", equalTo(false))
                    .body("books[8].isAvailable", equalTo(false))
                    .body("total", equalTo(9))
                    .body("totalPages", equalTo(1));
        }

        @Test
        void getPagedBooksWithFilters_WhenFilterContainsInvalidCategory_ReturnsNoBooks() {
            String filters = """
                    {"categoryFilter": ["Invalid"]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(0))
                    .body("total", equalTo(0))
                    .body("totalPages", equalTo(0));
        }

        @Test
        void getPagedBooksWithFilters_WhenCategoryFilterContainsEmptyString_ReturnsNoBooks() {
            String filters = """
                    {"categoryFilter":[""]}
                    """;

            BookApiTestClient.getPagedBooksWithFilters(filters)
                    .statusCode(HttpStatus.OK.value())
                    .body("books.size()", equalTo(0))
                    .body("total", equalTo(0))
                    .body("totalPages", equalTo(0));
        }
    }

    @Test
    void shouldReturnActiveReservationsForValidBookId() throws JSONException {

        File coverImageFile = new File("./src/test/resources/assets/book_cover.png");
        Map<String, String> bookData = BookTestDataFactory.getSampleBookFormData();

        Integer officeId = OfficeApiTestClient.getOffices()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("[0].officeId");

        JSONArray newBookCopies = BookTestDataFactory.createBookCopies(Map.of(officeId, 2));
        bookData.put("newBookCopies", newBookCopies.toString());

        Integer bookId = BookApiTestClient.addBook(bookData, coverImageFile)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("bookId");

        String firstName = "testfirstname";
        String lastName = "testlastname";
        String officeName = "Kaunas";
        LocalDate borrowedFrom = LocalDate.now();
        LocalDate returnDate = LocalDate.of(2024, 12, 15);

        NewBorrowedBookDto newBorrowedBookDto = new NewBorrowedBookDto(officeId, returnDate);
        BookApiTestClient.createBorrowedBook(bookId, newBorrowedBookDto);

        BookApiTestClient.getActiveReservationsForBook(bookId)
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("[0].firstName", equalTo(firstName))
                .body("[0].lastName", equalTo(lastName))
                .body("[0].officeName", equalTo(officeName))
                .body("[0].borrowedFrom", equalTo(borrowedFrom.toString()))
                .body("[0].returnDate", equalTo(returnDate.toString()));
    }

    @Test
    void shouldReturnNotFoundWhenBookIdNotExist() {
        Integer nonExistentBookId = 555;
        BookApiTestClient.getActiveReservationsForBook(nonExistentBookId)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}