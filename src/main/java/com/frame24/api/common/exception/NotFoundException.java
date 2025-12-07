package com.frame24.api.common.exception;

/**
 * Exceção para recurso não encontrado (404).
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, Object identifier) {
        super(String.format("%s não encontrado: %s", resourceName, identifier));
    }
}
