package com.example.demo.repository.specification;

import com.example.demo.model.BookCopyEntity;
import com.example.demo.model.BookEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookSpecification {
    public static Specification<BookEntity> filterBooks(Boolean isAvailableFilter, List<Integer> officeIdList, List<String> categoryFilter) {
        return (root, query, builder) -> {
            Join<BookEntity, BookCopyEntity> bookCopiesJoin = root.join("bookCopies", JoinType.LEFT);

            // Category filter
            Predicate categoryPredicate = builder.conjunction();
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                categoryPredicate = root.get("category").in(categoryFilter);
            }

            // Availability filter: (true, false, null)
            Predicate isAvailablePredicate = builder.conjunction();
            if (isAvailableFilter != null) {
                if (isAvailableFilter) {
                    isAvailablePredicate = builder.and(builder.equal(bookCopiesJoin.get("isAvailable"), true),
                            officeIdList != null && !officeIdList.isEmpty() ? bookCopiesJoin.get("office").get("officeId").in(officeIdList) : builder.conjunction());
                } else {
                    // Books with no copies (this is for when there is an Instance of a book, but it has no copies in database)
                    Subquery<Long> noCopiesSubquery = query.subquery(Long.class);
                    Root<BookCopyEntity> noCopiesSubRoot = noCopiesSubquery.from(BookCopyEntity.class);
                    noCopiesSubquery.select(noCopiesSubRoot.get("book").get("id")).where(builder.equal(noCopiesSubRoot.get("book"), root));

                    Predicate booksWithNoCopies = builder.not(builder.exists(noCopiesSubquery));

                    // Books with unavailable copies (this is normal case when there are copies, but they are all unavailable)
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<BookCopyEntity> subRoot = subquery.from(BookCopyEntity.class);
                    subquery.select(subRoot.get("book").get("bookId")).where(builder.and(builder.equal(subRoot.get("isAvailable"), true),
                            officeIdList != null && !officeIdList.isEmpty() ? subRoot.get("office").get("officeId").in(officeIdList) : builder.conjunction()));

                    Predicate excludeAvailableBooks = builder.not(bookCopiesJoin.get("book").get("bookId").in(subquery));

                    // We return here both, books with no copies and books with unavailable copies
                    isAvailablePredicate = builder.or(excludeAvailableBooks, booksWithNoCopies);
                }
            }
            query.distinct(true);
            return builder.and(categoryPredicate, isAvailablePredicate);
        };
    }
}