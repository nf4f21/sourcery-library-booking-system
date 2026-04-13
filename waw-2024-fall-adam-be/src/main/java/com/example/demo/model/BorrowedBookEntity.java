package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "borrowed_books")
public class BorrowedBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrowed_id")
    private Integer borrowedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopyEntity bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status;

    @Column(name = "borrowed_from", nullable = false)
    private LocalDate borrowedFrom;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "extensions_count", nullable = false)
    private Integer extensionsCount;
}