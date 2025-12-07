package com.frame24.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para CNPJ brasileiro.
 * Valida formato e dígitos verificadores conforme algoritmo oficial.
 */
public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

    private boolean optional;

    @Override
    public void initialize(ValidCnpj constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null ou vazio é válido se optional = true
        if (value == null || value.trim().isEmpty()) {
            return optional;
        }

        // Remove formatação
        String cnpj = value.replaceAll("[^0-9]", "");

        // Deve ter exatamente 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        // CNPJs conhecidos como inválidos (todos os dígitos iguais)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights1[i];
        }
        int firstDigit = sum % 11;
        firstDigit = (firstDigit < 2) ? 0 : (11 - firstDigit);

        // Verifica primeiro dígito
        if (firstDigit != Character.getNumericValue(cnpj.charAt(12))) {
            return false;
        }

        // Calcula segundo dígito verificador
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights2[i];
        }
        int secondDigit = sum % 11;
        secondDigit = (secondDigit < 2) ? 0 : (11 - secondDigit);

        // Verifica segundo dígito
        return secondDigit == Character.getNumericValue(cnpj.charAt(13));
    }
}
