package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DirectionValidator implements ConstraintValidator<ValidSortDirection, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equalsIgnoreCase("asc") || value.equalsIgnoreCase("desc");
    }
}
