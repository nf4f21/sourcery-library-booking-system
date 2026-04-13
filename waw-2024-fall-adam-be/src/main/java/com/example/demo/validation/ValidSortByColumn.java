package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SortByColumnValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortByColumn {

    String message() default "Not correct value. You can sort by: borrowedId, borrowedFrom, returnDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedFields() default {};
}
