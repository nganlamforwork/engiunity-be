/**
 * Author: lamlevungan
 * Date: 21/04/2025
 **/
package com.codewithmosh.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowercaseValidator implements ConstraintValidator<Lowercase, String>{
    @Override
    public void initialize(Lowercase constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;

        return value.equals(value.toLowerCase());
    }
}
