package com.example.demo.service;

import com.example.demo.dto.BookCopyDto;
import com.example.demo.dto.NewBookCopyDto;
import com.example.demo.exception.BookCopyNotFoundException;
import com.example.demo.exception.BookCopyUnavailableException;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.OfficeNotFoundException;
import com.example.demo.mapper.BookCopyMapper;
import com.example.demo.model.BookCopyEntity;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OfficeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final OfficeRepository officeRepository;
    private final BookCopyMapper bookCopyMapper;

    @Transactional
    public List<BookCopyDto> saveNewBookCopy(NewBookCopyDto newBookCopyDto) {

        var book = bookRepository.findByBookId(newBookCopyDto.bookId()).orElseThrow(BookNotFoundException::new);
        var office = officeRepository.findById(newBookCopyDto.officeId())
                .orElseThrow(() -> new OfficeNotFoundException("Not found office with id " + newBookCopyDto.officeId()));
        var copies = new ArrayList<BookCopyDto>();
        for (int i = 0; i < newBookCopyDto.copyCount(); i++) {
            BookCopyEntity newBookCopy = BookCopyEntity.builder()
                    .isAvailable(true)
                    .book(book)
                    .office(office)
                    .build();
            bookCopyRepository.save(newBookCopy);
            copies.add(bookCopyMapper.mapEntityToBookCopyDto(newBookCopy));
        }

        return copies;
    }

    public void deleteBookCopy(Integer bookCopyId) {
        BookCopyEntity bookCopyEntity = bookCopyRepository
                .findById(bookCopyId)
                .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
        if (!bookCopyEntity.isAvailable()) {
            throw new BookCopyUnavailableException(bookCopyId);
        }
        bookCopyRepository.delete(bookCopyEntity);
    }
}
