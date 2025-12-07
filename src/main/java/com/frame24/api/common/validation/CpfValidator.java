package com.frame24.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para CPF brasileiro.
 * Valida formato e dígitos verificadores conforme algoritmo oficial.
 */
public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    private boolean optional;

    @Override
    public void initialize(ValidCpf constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null ou vazio é válido se optional = true
        if (value == null || value.trim().isEmpty()) {
            return optional;
        }

        // Remove formatação
        String cpf = value.replaceAll("[^0-9]", "");

        // Deve ter exatamente 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // CPFs conhecidos como inválidos (todos os dígitos iguais)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        // Verifica primeiro dígito
        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // Calcula segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        // Verifica segundo dígito
        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
}
