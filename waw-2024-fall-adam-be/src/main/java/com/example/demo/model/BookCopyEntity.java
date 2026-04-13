package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "books_copy")
public class BookCopyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_copy_id")
    private Integer bookCopyId;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private OfficeEntity office;

}
