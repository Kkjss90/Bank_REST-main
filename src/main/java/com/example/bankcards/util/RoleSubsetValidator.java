package com.example.bankcards.util;

import com.example.bankcards.entity.enums.RoleEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * The type Role subset validator.
 */
public class RoleSubsetValidator implements ConstraintValidator<RoleSubset, RoleEnum> {
    private RoleEnum[] subset;

    /**
     * Initialize.
     *
     * @param constraint the constraint
     */
    @Override
    public void initialize(RoleSubset constraint) {
        this.subset = constraint.anyOf();
    }

    /**
     * Is valid boolean.
     *
     * @param value   the value
     * @param context the context
     * @return the boolean
     */
    @Override
    public boolean isValid(RoleEnum value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}