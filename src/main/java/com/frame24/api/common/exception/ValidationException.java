package com.frame24.api.common.exception;

/**
 * Exceção para erros de validação de negócio (422).
 */
public class ValidationException extends BusinessException {

    private final String field;

    public ValidationException(String message) {
        super(message);
        this.field = null;
    }

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
