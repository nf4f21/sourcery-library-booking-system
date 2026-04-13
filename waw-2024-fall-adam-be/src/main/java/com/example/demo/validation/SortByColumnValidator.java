package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class SortByColumnValidator implements ConstraintValidator<ValidSortByColumn, String> {

    private List<String> allowedSortColumns;

    @Override
    public void initialize(ValidSortByColumn constraintAnnotation) {
        allowedSortColumns = Arrays.asList(constraintAnnotation.allowedFields());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return allowedSortColumns.stream()
                .anyMatch(v -> v.equalsIgnoreCase(value));
    }
}
