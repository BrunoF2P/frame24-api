package com.frame24.api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valida CNPJ brasileiro (formato e dígitos verificadores).
 * Aceita CNPJ formatado (XX.XXX.XXX/XXXX-XX) ou apenas números (14 dígitos).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CnpjValidator.class)
@Documented
public @interface ValidCnpj {

    String message() default "CNPJ inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Se true, aceita valores null ou vazios (opcional).
     */
    boolean optional() default false;
}
