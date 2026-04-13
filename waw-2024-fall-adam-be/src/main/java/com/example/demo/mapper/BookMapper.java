package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookPreviewDto;
import com.example.demo.dto.NewBookDto;
import com.example.demo.model.BookEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = BookCopyMapper.class)
public interface BookMapper {

    @Mapping(target = "bookCopies", source = "bookCopies")
    BookDto mapBookEntityToDto(BookEntity book);

    @Mapping(target = "coverImage", source = "coverImage")
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    BookEntity mapNewBookDtoToEntity(NewBookDto book);

    @Mapping(target = "coverImage", source = "coverImage")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookFromNewBookDto(NewBookDto updatedBookDetails, @MappingTarget BookEntity bookToUpdate);

    default BookPreviewDto mapBookEntityToPreviewDto(BookEntity book, Boolean isAvailableFilter, List<Integer> officeIdFilter) {
        boolean availability = isAvailableFilter == null ? isBookAvailable(book, officeIdFilter) : isAvailableFilter;

        return new BookPreviewDto(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverImage(),
                availability
        );
    }

    default boolean isBookAvailable(BookEntity book, List<Integer> officeIds) {
        return book.getBookCopies() != null && book.getBookCopies().stream().anyMatch(
                bookCopy -> bookCopy.isAvailable() && (officeIds == null || officeIds.isEmpty() || officeIds.contains(bookCopy.getOffice().getOfficeId()))
        );
    }

    default byte[] map(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("Cover image is required.");
        }
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to byte[]", e);
        }
    }
}
    
