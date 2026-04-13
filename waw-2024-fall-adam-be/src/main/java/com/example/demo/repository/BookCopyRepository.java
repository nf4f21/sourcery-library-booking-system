package com.example.demo.repository;

import com.example.demo.model.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopyEntity, Integer> {
    @Query("SELECT b FROM BookCopyEntity b WHERE b.book.bookId = :bookId AND b.isAvailable = true")
    List<BookCopyEntity> findAvailableBookCopiesByBookId(Integer bookId);

    List<BookCopyEntity> findByBookId(Integer bookId);

    @Modifying
    @Query("UPDATE BookCopyEntity b SET b.isAvailable = :availability WHERE b.bookCopyId = :bookCopyId")
    void changeAvailability(Integer bookCopyId, Boolean availability);

    Optional<BookCopyEntity> findFirstByBook_BookIdAndIsAvailableTrueAndOffice_OfficeId(Integer bookId, Integer officeId);

    boolean existsByBook_BookIdAndIsAvailableTrue(Integer bookId);
}
