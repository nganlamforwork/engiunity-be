package com.codewithmosh.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: lamlevungan
 * Date: 21/04/2025
 **/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowercaseValidator.class)
public @interface Lowercase {
    String message() default "must be lowercase";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
