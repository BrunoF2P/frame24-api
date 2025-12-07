package com.frame24.api.common.exception;

/**
 * Exceção para conflito de recursos (409).
 * Usado quando um recurso já existe.
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String field, Object value) {
        super(String.format("%s já cadastrado com %s: %s", resourceName, field, value));
    }
}
