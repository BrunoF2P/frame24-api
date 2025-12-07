package com.frame24.api.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Resposta padronizada de erro da API.
 * Segue padrão RFC 7807 (Problem Details for HTTP APIs).
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "error", "timestamp", "traceId"})
@Schema(description = "Resposta de erro padronizada")
public class ApiErrorResponse {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "false")
    @Builder.Default
    private final boolean success = false;

    @Schema(description = "Detalhes do erro")
    private final ErrorDetails error;

    @Schema(description = "Timestamp do erro", example = "2024-12-06T22:30:00Z")
    private final Instant timestamp;

    @Schema(description = "ID para rastreamento em logs", example = "c0b74c18")
    private final String traceId;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"code", "title", "detail", "suggestion", "fields"})
    @Schema(description = "Detalhes do erro")
    public static class ErrorDetails {

        @Schema(description = "Código do erro para referência", example = "VALIDATION_ERROR")
        private final String code;

        @Schema(description = "Título curto do erro", example = "Erro de Validação")
        private final String title;

        @Schema(description = "Descrição detalhada do problema", example = "O CNPJ informado não foi encontrado na Receita Federal")
        private final String detail;

        @Schema(description = "Sugestão para resolver o problema", example = "Verifique se o CNPJ está correto e tente novamente")
        private final String suggestion;

        @Schema(description = "Campos com erro de validação")
        private final List<FieldError> fields;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Erro de validação de campo específico")
    public static class FieldError {

        @Schema(description = "Nome do campo", example = "cnpj")
        private final String field;

        @Schema(description = "Mensagem de erro", example = "CNPJ inválido")
        private final String message;

        @Schema(description = "Valor informado", example = "123")
        private final Object value;
    }

    /**
     * Factory method para criar resposta de erro de validação.
     */
    public static ApiErrorResponse validationError(String detail, String traceId) {
        return ApiErrorResponse.builder()
                .error(ErrorDetails.builder()
                        .code("VALIDATION_ERROR")
                        .title("Erro de Validação")
                        .detail(detail)
                        .suggestion("Verifique os dados informados e tente novamente")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();
    }

    /**
     * Factory method para criar resposta de conflito.
     */
    public static ApiErrorResponse conflict(String detail, String traceId) {
        return ApiErrorResponse.builder()
                .error(ErrorDetails.builder()
                        .code("CONFLICT")
                        .title("Recurso Já Existe")
                        .detail(detail)
                        .suggestion("Utilize dados diferentes ou recupere o acesso ao recurso existente")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();
    }

    /**
     * Factory method para criar resposta de serviço externo.
     */
    public static ApiErrorResponse externalService(String serviceName, String detail, String traceId) {
        return ApiErrorResponse.builder()
                .error(ErrorDetails.builder()
                        .code("EXTERNAL_SERVICE_ERROR")
                        .title("Serviço Temporariamente Indisponível")
                        .detail(String.format("Falha ao comunicar com %s: %s", serviceName, detail))
                        .suggestion(
                                "Aguarde alguns instantes e tente novamente. Se o problema persistir, entre em contato com o suporte.")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();
    }

    /**
     * Factory method para criar resposta de erro interno.
     */
    public static ApiErrorResponse internalError(String traceId) {
        return ApiErrorResponse.builder()
                .error(ErrorDetails.builder()
                        .code("INTERNAL_ERROR")
                        .title("Erro Interno do Servidor")
                        .detail("Ocorreu um erro inesperado. Nossa equipe foi notificada.")
                        .suggestion(
                                "Tente novamente em alguns instantes. Se o problema persistir, entre em contato informando o código: "
                                        + traceId)
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();
    }
}
