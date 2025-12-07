package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO de response para tipo de projeção.
 */
@Schema(description = "Dados de um tipo de projeção")
public record ProjectionTypeResponse(
        @Schema(description = "ID do tipo de projeção", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do tipo de projeção", example = "IMAX") String name,

        @Schema(description = "Descrição do tipo de projeção", example = "Projeção em tela gigante IMAX") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "15.00") BigDecimal additionalValue,

        @Schema(description = "Data de criação") Instant createdAt) {
}
