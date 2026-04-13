package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.BasicOfficeDto;
import com.example.demo.dto.BookCopyAvailabilityInOfficeDto;
import com.example.demo.service.OfficeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = DemoApplication.PATH_V1 + "/offices")
public class OfficeController {
    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping
    public List<BasicOfficeDto> getOffices(@RequestParam(defaultValue = "0") int pageNumber,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        return officeService.getOffices(pageNumber, pageSize);
    }

    @GetMapping(value = "/book/{bookId}")
    public List<BookCopyAvailabilityInOfficeDto> getBookCopyAvailabilityInEachOfficeInfo(@PathVariable Integer bookId) {
        return officeService.getBookCopyAvailabilityInEachOffice(bookId);
    }
}
