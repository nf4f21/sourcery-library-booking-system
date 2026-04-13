package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BookFiltersDto {
    private Boolean isAvailableFilter;

    private List<Integer> officeIdFilter;

    private List<String> categoryFilter;
}
