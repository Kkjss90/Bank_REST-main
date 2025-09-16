package com.example.bankcards.util;

import com.example.bankcards.entity.enums.RoleEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Role subset.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleSubsetValidator.class)
public @interface RoleSubset {
    /**
     * Any of role enum [ ].
     *
     * @return the role enum [ ]
     */
    RoleEnum[] anyOf();

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "Роль должна быть одной из: {anyOf}";

    /**
     * Groups class [ ].
     *
     * @return the class [ ]
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     *
     * @return the class [ ]
     */
    Class<? extends Payload>[] payload() default {};
}
