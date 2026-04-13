package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DirectionValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortDirection {

    String message() default "Not correct value of direction, you can only use ASC or DESC";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
