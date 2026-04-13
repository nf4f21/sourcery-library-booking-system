package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.*;
import com.example.demo.mapper.BorrowedBookMapper;
import com.example.demo.model.BookCopyEntity;
import com.example.demo.model.BorrowStatus;
import com.example.demo.model.BorrowedBookEntity;
import com.example.demo.model.UserEntity;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BorrowedBookRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static com.example.demo.constants.BorrowedBookConstants.MAX_EXTENSIONS_COUNT;
import static com.example.demo.constants.BorrowedBookConstants.MAX_EXTENSION_LENGTH_DAYS;

@Service
@AllArgsConstructor
public class BorrowedBookService {

    private final BorrowedBookRepository borrowedBookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowedBookMapper borrowedBookMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public BorrowedBooksPaginatedDto getBorrowedBooks(String sortBy, String direction, int pageNumber, int pageSize, BorrowStatus status) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Sort sort = Sort.by(sortDirection, sortBy);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        String email = userService.getAuthenticatedUserEmail();

        UserEntity userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> getEmailNotFoundException(email));
        Integer userId = userByEmail.getUserId();

        Page<BorrowedBookEntity> borrowedBookEntitiesPage = borrowedBookRepository
                .findAllByUserId(userId, pageRequest, status);
        List<BorrowedBookDto> borrowedBooks = borrowedBookEntitiesPage.stream()
                .map(borrowedBookMapper::mapBorrowedBookEntityToDto)
                .toList();
        long borrowedBookCount = borrowedBookEntitiesPage.getTotalElements();
        return BorrowedBooksPaginatedDto.builder()
                .borrowedBooks(borrowedBooks)
                .borrowedBooksCount(borrowedBookCount)
                .build();
    }

    @Transactional
    public BorrowedBookDto saveNewBookBorrowing(NewBorrowedBookDto newBorrowing, Integer bookId, String email) {
        BookCopyEntity bookCopyToBorrow = bookCopyRepository
                .findFirstByBook_BookIdAndIsAvailableTrueAndOffice_OfficeId(bookId, newBorrowing.officeId())
                .orElseThrow(() -> new BookCopyNotFoundException(bookId, newBorrowing.officeId()));
        UserEntity userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> getEmailNotFoundException(email));

        BorrowedBookEntity borrowing = BorrowedBookEntity.builder()
                .status(BorrowStatus.BORROWED)
                .bookCopy(bookCopyToBorrow)
                .user(userByEmail)
                .borrowedFrom(LocalDate.now())
                .returnDate(newBorrowing.returnDate())
                .extensionsCount(0)
                .build();
        BorrowedBookEntity savedBorrowing = borrowedBookRepository.save(borrowing);
        bookCopyToBorrow.setAvailable(false);

        return borrowedBookMapper.mapBorrowedBookEntityToDto(savedBorrowing);
    }

    @Transactional
    public BorrowedBookDto editBorrowedBook(Integer borrowedId, UpdateBorrowedBookDto updateBorrowedBookDto) {
        String email = userService.getAuthenticatedUserEmail();

        UserEntity userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> getEmailNotFoundException(email));

        BorrowedBookEntity borrowedBook = borrowedBookRepository
                .findById(borrowedId)
                .orElseThrow(BorrowedBookNotFoundException::new);

        if (!Objects.equals(borrowedBook.getUser().getUserId(), userByEmail.getUserId())) {
            throw new BorrowedBookNotFoundException();
        }
        if (borrowedBook.getStatus() == BorrowStatus.RETURNED) {
            throw new BorrowedBookExtensionException();
        }

        Integer extensionsCount = borrowedBook.getExtensionsCount();
        if (extensionsCount == MAX_EXTENSIONS_COUNT) {
            throw new InvalidExtensionsCountOfNewReturnDateException(
                    "You can not set new return date, max extension count is 2 ");
        }

        LocalDate newReturnDate = updateBorrowedBookDto.returnDate();
        LocalDate oldReturnDate = borrowedBook.getReturnDate();
        long daysBetweenDates = ChronoUnit.DAYS.between(oldReturnDate, newReturnDate);

        if (newReturnDate.isBefore(LocalDate.now()) || daysBetweenDates > MAX_EXTENSION_LENGTH_DAYS) {
            throw new InvalidUpdateNewReturnDateException(
                    "Insert invalid new return date, max extension length is " + MAX_EXTENSION_LENGTH_DAYS + " days");
        }

        borrowedBook.setReturnDate(newReturnDate);
        borrowedBook.setExtensionsCount(extensionsCount + 1);
        borrowedBookRepository.save(borrowedBook);
        return borrowedBookMapper.mapBorrowedBookEntityToDto(borrowedBook);
    }

    @Transactional
    public BorrowedBookDto returnBorrowedBook(Integer borrowedId, String email) {
        UserEntity userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> getEmailNotFoundException(email));

        BorrowedBookEntity borrowedBook = borrowedBookRepository
                .findByBorrowedIdAndUser_userId(borrowedId, userByEmail.getUserId())
                .orElseThrow(BorrowedBookNotFoundException::new);

        if (borrowedBook.getStatus() == BorrowStatus.RETURNED) {
            throw new BorrowedBookAlreadyReturnedException();
        }

        borrowedBook.setStatus(BorrowStatus.RETURNED);
        borrowedBook.getBookCopy().setAvailable(true);

        BorrowedBookEntity returnedBook = borrowedBookRepository.save(borrowedBook);

        return borrowedBookMapper.mapBorrowedBookEntityToDto(returnedBook);
    }

    public List<ActiveBookReservationsDto> getActiveReservationsForBook(Integer bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);
        BorrowStatus status = BorrowStatus.BORROWED;

        return borrowedBookRepository.findActiveReservationsByBookId(bookId, status);
    }

    private EmailNotFoundException getEmailNotFoundException(String email) {
        return new EmailNotFoundException("Not found user with that email: " + email);
    }
}
