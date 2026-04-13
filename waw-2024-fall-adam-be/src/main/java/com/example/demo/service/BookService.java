package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.InvalidBookFiltersException;
import com.example.demo.mapper.BookCopyMapper;
import com.example.demo.mapper.BookMapper;
import com.example.demo.model.BookCopyEntity;
import com.example.demo.model.BookEntity;
import com.example.demo.model.OfficeEntity;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OfficeRepository;
import com.example.demo.repository.specification.BookSpecification;
import com.example.demo.util.FileValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;
    private final FileValidator fileValidator;
    private final BookCopyRepository bookCopyRepository;
    private final OfficeRepository officeRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    @Transactional
    public BookDto saveNewBook(NewBookDto newBook) {
        // Validate the file type of the cover image
        fileValidator.isCorrectTypeFile(newBook.coverImage());

        // Save the book entity
        BookEntity savedBook = bookRepository.save(bookMapper.mapNewBookDtoToEntity(newBook));
        log.info("New book with ID " + savedBook.getBookId() + " has been saved.");


        // Create and save book copies for each office
        List<BookCopyEntity> savedCopies = new ArrayList<>(); // Initialize the list to store saved copies


        // Create and save book copies for each office
        if (newBook.newBookCopies() != null) {
            for (NewBookCopyDto newBookCopy : newBook.newBookCopies()) {
                // Fetch OfficeEntity by officeId
                if (newBookCopy.officeId() < 0) {
                    throw new IllegalArgumentException("Office ID cannot be negative: " + newBookCopy.officeId());
                }
                OfficeEntity officeEntity = officeRepository.findById(newBookCopy.officeId())
                        .orElseThrow(() -> new IllegalArgumentException("Office with ID " + newBookCopy.officeId() + " not found"));
                log.info("Office ID: {} fetched successfully.", newBookCopy.officeId());

                // Create book copies for the given office
                for (int i = 0; i < newBookCopy.copyCount(); i++) {
                    BookCopyEntity bookCopyEntity = new BookCopyEntity();
                    bookCopyEntity.setBook(savedBook);
                    bookCopyEntity.setOffice(officeEntity);
                    bookCopyEntity.setAvailable(true); // Initial availability set to true
                    bookCopyRepository.save(bookCopyEntity);
                    log.info("Book copy saved for Office ID " + officeEntity.getOfficeId());
                    savedBook.getBookCopies().add(bookCopyEntity); // Add the saved copy to the book's copies list
                }
            }
        }
        savedBook.setBookCopies(savedCopies); // Update savedBook with the saved copies
        BookDto bookDto = bookMapper.mapBookEntityToDto(savedBook);

        List<BookCopyEntity> bookCopiesEntity = bookCopyRepository.findByBookId(savedBook.getBookId());
        List<BookCopyDto> bookCopies = bookCopiesEntity.stream()
                .map(bookCopyMapper::mapEntityToBookCopyDto)
                .toList();

        bookDto = new BookDto(
                bookDto.bookId(),
                bookDto.coverImage(),
                bookDto.title(),
                bookDto.author(),
                bookDto.description(),
                bookDto.format(),
                bookDto.numberOfPages(),
                bookDto.publicationDate(),
                bookDto.publisher(),
                bookDto.isbn(),
                bookDto.editionLanguage(),
                bookDto.series(),
                bookDto.category(),
                bookCopies
        );

        return bookDto;
    }

    /**
     * @return An updated version of the book, or null if this book does not exist
     */
    @Transactional
    public BookDto editBookDetails(Integer bookId, NewBookDto updatedBookDetails) {
        // check if the book exists
        BookEntity bookEntity = bookRepository
                .findById(bookId)
                .orElseThrow(BookNotFoundException::new);
        // validate image
        MultipartFile coverImage = updatedBookDetails.coverImage();
        if (coverImage != null) {
            fileValidator.isCorrectTypeFile(coverImage);
        }
        // update the book
        bookMapper.updateBookFromNewBookDto(updatedBookDetails, bookEntity);
        // save updated book
        bookRepository.save(bookEntity);
        return bookMapper.mapBookEntityToDto(bookEntity);
    }

    @Transactional
    public BookDto getBookById(Integer bookId) {
        try {
            Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
            return bookEntity
                    .map(bookMapper::mapBookEntityToDto)
                    .orElseThrow(BookNotFoundException::new);
        } catch (Exception e) {
            throw new RuntimeException("Error getting book by id: " + bookId, e);
        }
    }

    @Transactional
    public BookPagedDto getPagedBooksWithFilters(Pageable pageable, String bookFilters) {
        BookFiltersDto mappedBookFilters;

        try {
            mappedBookFilters = objectMapper.readValue(bookFilters, BookFiltersDto.class);
        } catch (JsonProcessingException e) {
            throw new InvalidBookFiltersException();
        }

        Page<BookEntity> page = bookRepository.findAll(BookSpecification.filterBooks(
                mappedBookFilters.getIsAvailableFilter(),
                mappedBookFilters.getOfficeIdFilter(),
                mappedBookFilters.getCategoryFilter()), pageable
        );

        List<BookPreviewDto> bookPreviewDtoGrid = page.getContent()
                .stream()
                .map(bookEntity -> bookMapper.mapBookEntityToPreviewDto(
                        bookEntity,
                        mappedBookFilters.getIsAvailableFilter(),
                        mappedBookFilters.getOfficeIdFilter()
                )).collect(Collectors.toList());

        return new BookPagedDto(bookPreviewDtoGrid, page.getTotalElements(), page.getTotalPages());
    }

    public boolean getBookAvailability(Integer bookId) {
        bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        return bookCopyRepository.existsByBook_BookIdAndIsAvailableTrue(bookId);
    }
}

