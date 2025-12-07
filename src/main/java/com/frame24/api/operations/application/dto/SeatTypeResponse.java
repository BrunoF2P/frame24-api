package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO de response para tipo de assento.
 */
@Schema(description = "Dados de um tipo de assento")
public record SeatTypeResponse(
        @Schema(description = "ID do tipo de assento", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do tipo de assento", example = "VIP") String name,

        @Schema(description = "Descrição do tipo de assento", example = "Assento reclinável de couro") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "10.00") BigDecimal additionalValue,

        @Schema(description = "Data de criação") Instant createdAt) {
}
