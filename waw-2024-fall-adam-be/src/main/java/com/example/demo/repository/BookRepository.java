package com.example.demo.repository;

import com.example.demo.model.BookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity> {

    @Query("SELECT b FROM BookEntity b")
    List<BookEntity> getBooks(Pageable pageable);

    // Find a book by its ISBN
    Optional<BookEntity> findByIsbn(String isbn);

    Optional<BookEntity> findByBookId(Integer bookId);
}
