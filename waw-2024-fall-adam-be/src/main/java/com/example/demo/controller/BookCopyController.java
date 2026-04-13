package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.BookCopyDto;
import com.example.demo.dto.NewBookCopyDto;
import com.example.demo.service.BookCopyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping(value = DemoApplication.PATH_V1 + "/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @PostMapping()
    public List<BookCopyDto> createNewBookCopy(@RequestBody NewBookCopyDto newCopy) {

        return bookCopyService.saveNewBookCopy(newCopy);
    }

    @DeleteMapping("/{bookCopyId}")
    public void deleteBookCopy(@PathVariable Integer bookCopyId) {
        bookCopyService.deleteBookCopy(bookCopyId);
    }
}
