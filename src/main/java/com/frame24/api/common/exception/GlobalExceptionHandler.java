package com.frame24.api.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Handler global para exceções da API.
 * Transforma exceções em respostas JSON padronizadas e profissionais.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("NOT_FOUND")
                        .title("Recurso Não Encontrado")
                        .detail(ex.getMessage())
                        .suggestion("Verifique se o identificador está correto")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.conflict(ex.getMessage(), traceId));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();

        List<ApiErrorResponse.FieldError> fields = ex.getField() != null
                ? List.of(ApiErrorResponse.FieldError.builder()
                .field(ex.getField())
                .message(ex.getMessage())
                .build())
                : null;

        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("VALIDATION_ERROR")
                        .title("Erro de Validação")
                        .detail(ex.getMessage())
                        .suggestion("Verifique os dados informados e tente novamente")
                        .fields(fields)
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(422).body(response);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalService(ExternalServiceException ex,
                                                                  HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[{}] Falha em serviço externo: {}", traceId, ex.getServiceName(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiErrorResponse.externalService(ex.getServiceName(), ex.getMessage(), traceId));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("BUSINESS_ERROR")
                        .title("Erro de Negócio")
                        .detail(ex.getMessage())
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex,
                                                                 HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Erro de autenticação: {}", traceId, ex.getMessage());

        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("UNAUTHORIZED")
                        .title("Não Autenticado")
                        .detail("Você precisa estar autenticado para acessar este recurso")
                        .suggestion("Faça login e tente novamente")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                               HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Erro de autorização: {}", traceId, ex.getMessage());

        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("FORBIDDEN")
                        .title("Acesso Negado")
                        .detail("Você não tem permissão para acessar este recurso")
                        .suggestion("Verifique suas permissões ou entre em contato com o administrador")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        List<ApiErrorResponse.FieldError> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ApiErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .value(error.getRejectedValue())
                        .build())
                .toList();

        var response = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("VALIDATION_ERROR")
                        .title("Erro de Validação")
                        .detail("Um ou mais campos possuem valores inválidos")
                        .suggestion("Corrija os campos indicados e tente novamente")
                        .fields(fields)
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(422).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[{}] Erro não tratado: {}", traceId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.internalError(traceId));
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
