package com.example.demo.repository;

import com.example.demo.dto.ActiveBookReservationsDto;
import com.example.demo.model.BorrowStatus;
import com.example.demo.model.BorrowedBookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBookEntity, Integer> {

    @Query("""
            SELECT b FROM BorrowedBookEntity b LEFT JOIN FETCH b.bookCopy bc LEFT JOIN FETCH bc.book book
            LEFT JOIN FETCH bc.office office LEFT JOIN FETCH b.user u
            WHERE u.userId = :userId
            AND (:status IS NULL OR b.status = :status)
            """)
    Page<BorrowedBookEntity> findAllByUserId(Integer userId, Pageable pageable, BorrowStatus status);

    @Query("""
            SELECT new com.example.demo.dto.ActiveBookReservationsDto(u.firstName, u.lastName, o.name, bb.borrowedFrom,
            bb.returnDate) FROM BorrowedBookEntity bb
            LEFT JOIN  bb.bookCopy bc
            LEFT JOIN  bc.office o
            LEFT JOIN bb.user u
            WHERE bc.book.bookId = :bookId
            AND bb.status = :status
            """)
    List<ActiveBookReservationsDto> findActiveReservationsByBookId(Integer bookId, BorrowStatus status);

    Optional<BorrowedBookEntity> findByBorrowedIdAndUser_userId(Integer borrowedId, Integer userId);
}