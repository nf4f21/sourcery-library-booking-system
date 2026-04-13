package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;

    public Integer getId() {
        return getBookId();
    }

    @Column(name = "cover_image", nullable = false)
    private byte[] coverImage;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "number_of_pages", nullable = false)
    private Integer numberOfPages; // we can also use String type here

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "edition_language", nullable = false)
    private String editionLanguage;

    @Column(name = "series", nullable = false)
    private String series;

    @Column(name = "category", nullable = false)
    private String category;  // could be also a separate entity

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookCopyEntity> bookCopies;

    // leave Book copies functionality for later

    // We can do it by creating a new entity called BookCopyEntity and having each copy correspond to a unique book_id, or by using simpler approach and having
    // a field in BookEntity called copies and storing the number of copies in the database. I am in favor of the first option, as it allows for more flexibility.
    // We can also add more information about each copy, such as the office where the book is currently located in.

    // The availability of a book can be determined by checking if the number of copies that are not currently borrowed by an employee is greater than 0.

    public List<BookCopyEntity> getBookCopies() {
        return bookCopies != null ? bookCopies : new ArrayList<>();
    }

    public void setBookCopies(List<BookCopyEntity> bookCopies) {
        this.bookCopies = bookCopies;
    }


}