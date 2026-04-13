package com.example.demo.service;

import com.example.demo.dto.BasicOfficeDto;
import com.example.demo.dto.BookCopyAvailabilityInOfficeDto;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.mapper.OfficeMapper;
import com.example.demo.model.BookCopyEntity;
import com.example.demo.model.OfficeEntity;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OfficeRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public OfficeService(OfficeRepository officeRepository, OfficeMapper officeMapper, BookRepository bookRepository, BookCopyRepository bookCopyRepository) {
        this.officeRepository = officeRepository;
        this.officeMapper = officeMapper;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    public List<BasicOfficeDto> getOffices(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return officeRepository.findAll(pageable)
                .stream()
                .map(officeMapper::mapOfficeEntityToDto)
                .toList();
    }

    @Transactional // to avoid LazyInitializationException when fetching the book copies
    public List<BookCopyAvailabilityInOfficeDto> getBookCopyAvailabilityInEachOffice(Integer bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        Map<OfficeEntity, Long> officeBookCopiesAvailability = getOfficeBookCopiesAvailability(bookId);

        addOfficesWithoutBookCopies(officeBookCopiesAvailability);

        return getBookCopyAvailabilityInOfficeDto(officeBookCopiesAvailability);
    }

    private Map<OfficeEntity, Long> getOfficeBookCopiesAvailability(Integer bookId) {
        List<BookCopyEntity> availableCopies = bookCopyRepository.findAvailableBookCopiesByBookId(bookId);

        return availableCopies.stream()
                .collect(Collectors.groupingBy(BookCopyEntity::getOffice, Collectors.counting()));
    }

    private void addOfficesWithoutBookCopies(Map<OfficeEntity, Long> officeBookCopiesAvailability) {
        List<OfficeEntity> officesWithoutBookCopies = officeRepository.findAll()
                .stream()
                .filter(office -> !officeBookCopiesAvailability.containsKey(office))
                .toList();

        officeBookCopiesAvailability.putAll(officesWithoutBookCopies.stream()
                .collect(Collectors.toMap(office -> office, office -> 0L)));

    }

    private List<BookCopyAvailabilityInOfficeDto> getBookCopyAvailabilityInOfficeDto(Map<OfficeEntity, Long> officeBookCopiesAvailability) {
        return officeBookCopiesAvailability.entrySet().stream()
                .map(entry -> {
                    OfficeEntity office = entry.getKey();

                    BookCopyAvailabilityInOfficeDto bookCopyAvailabilityInOfficeDto = officeMapper.mapOfficeEntityToDetailedDto(office);

                    int availableCopies = entry.getValue().intValue();

                    return new BookCopyAvailabilityInOfficeDto(
                            bookCopyAvailabilityInOfficeDto.basicOffice(),
                            availableCopies,
                            bookCopyAvailabilityInOfficeDto.address()
                    );
                })
                .sorted(Comparator.comparing(BookCopyAvailabilityInOfficeDto::copiesAvailable).reversed()
                        .thenComparing(dto -> dto.basicOffice().officeId()))
                .collect(Collectors.toList());
    }
}
