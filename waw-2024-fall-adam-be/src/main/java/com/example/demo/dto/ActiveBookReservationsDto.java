package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActiveBookReservationsDto {
    private String firstName;
    private String lastName;
    private String officeName;
    private LocalDate borrowedFrom;
    private LocalDate returnDate;

    public ActiveBookReservationsDto(String firstName, String lastName, String officeName, LocalDate borrowedFrom,
                                     LocalDate returnDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.officeName = officeName;
        this.borrowedFrom = borrowedFrom;
        this.returnDate = returnDate;
    }
}
