package com.example.demo.mapper;

import com.example.demo.dto.BookCopyDto;
import com.example.demo.model.BookCopyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface BookCopyMapper {

    @Mapping(source = "office.officeId", target = "officeId")
    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "available", target = "isAvailable")
    BookCopyDto mapEntityToBookCopyDto(BookCopyEntity bookCopyEntity);
}
