package com.example.bankcards.util;

import com.example.bankcards.entity.enums.RoleEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleSubsetValidator implements ConstraintValidator<RoleSubset, RoleEnum> {
    private RoleEnum[] subset;

    @Override
    public void initialize(RoleSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(RoleEnum value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}