package com.frame24.api.common.exception;

/**
 * Exceção para falha em serviço externo (502).
 */
public class ExternalServiceException extends BusinessException {

    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super(String.format("Falha ao comunicar com %s: %s", serviceName, message));
        this.serviceName = serviceName;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("Falha ao comunicar com %s: %s", serviceName, message), cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
