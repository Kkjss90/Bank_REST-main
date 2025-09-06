package com.example.bankcards.util;

import com.example.bankcards.entity.enums.RoleEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleSubsetValidator.class)
public @interface RoleSubset {
    RoleEnum[] anyOf();
    String message() default "Роль должна быть одной из: {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
