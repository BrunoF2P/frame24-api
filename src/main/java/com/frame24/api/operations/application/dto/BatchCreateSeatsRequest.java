package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para criação de assentos em lote (matriz).
 */
@Schema(description = "Dados para criar assentos em lote (matriz)")
public record BatchCreateSeatsRequest(
        @NotNull(message = "ID da sala é obrigatório") @Schema(description = "ID da sala", example = "1234567890123456789") Long roomId,

        @NotNull(message = "Número de fileiras é obrigatório") @Min(value = 1, message = "Mínimo 1 fileira") @Max(value = 50, message = "Máximo 50 fileiras") @Schema(description = "Quantidade de fileiras a gerar", example = "10") Integer rows,

        @NotNull(message = "Número de colunas é obrigatório") @Min(value = 1, message = "Mínimo 1 coluna") @Max(value = 50, message = "Máximo 50 colunas") @Schema(description = "Quantidade de assentos por fileira", example = "15") Integer columns,

        @Schema(description = "ID do tipo de assento padrão para todos", example = "1234567890123456789") Long defaultSeatTypeId,

        @Schema(description = "Padrão de nomenclatura das fileiras (ALPHABETIC, NUMERIC, REVERSE_ALPHABETIC)", example = "ALPHABETIC", defaultValue = "ALPHABETIC") RowNamingPattern rowNamingPattern,

        @Schema(description = "Iniciar numeração das colunas em", example = "1", defaultValue = "1") Integer startColumnNumber) {
    public enum RowNamingPattern {
        ALPHABETIC, // A, B, C...
        NUMERIC, // 1, 2, 3...
        REVERSE_ALPHABETIC // ..., C, B, A (começa do fundo)
    }
}
