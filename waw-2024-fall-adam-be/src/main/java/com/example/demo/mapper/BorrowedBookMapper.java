package com.example.demo.mapper;

import com.example.demo.dto.BorrowedBookDto;
import com.example.demo.model.BorrowedBookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface BorrowedBookMapper {

    @Mapping(source = "bookCopy.book.title", target = "title")
    @Mapping(source = "bookCopy.book.author", target = "author")
    @Mapping(source = "bookCopy.book.coverImage", target = "coverImage")
    @Mapping(source = "bookCopy.office.name", target = "officeName")
    @Mapping(source = "bookCopy.bookCopyId", target = "bookCopyId")
    @Mapping(source = "user.userId", target = "userId")
    BorrowedBookDto mapBorrowedBookEntityToDto(BorrowedBookEntity borrowedBookEntity);

}
