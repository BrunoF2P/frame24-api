package com.frame24.api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valida CPF brasileiro (formato e dígitos verificadores).
 * Aceita CPF formatado (XXX.XXX.XXX-XX) ou apenas números (11 dígitos).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class)
@Documented
public @interface ValidCpf {

    String message() default "CPF inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Se true, aceita valores null ou vazios (opcional).
     */
    boolean optional() default true;
}
