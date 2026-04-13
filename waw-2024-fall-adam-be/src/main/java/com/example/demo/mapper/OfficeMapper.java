package com.example.demo.mapper;

import com.example.demo.dto.BasicOfficeDto;
import com.example.demo.dto.BookCopyAvailabilityInOfficeDto;
import com.example.demo.formatter.AddressFormatter;
import com.example.demo.model.OfficeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = AddressFormatter.class)
public interface OfficeMapper {
    BasicOfficeDto mapOfficeEntityToDto(OfficeEntity office);

    @Mapping(source = "office", target = "basicOffice")
    @Mapping(source = "office.address", target = "address", qualifiedByName = "formatAddress")
    BookCopyAvailabilityInOfficeDto mapOfficeEntityToDetailedDto(OfficeEntity office);
}
