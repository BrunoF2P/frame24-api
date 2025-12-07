package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO de response para tipo de áudio.
 */
@Schema(description = "Dados de um tipo de áudio")
public record AudioTypeResponse(
        @Schema(description = "ID do tipo de áudio", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do tipo de áudio", example = "Dolby Atmos") String name,

        @Schema(description = "Descrição do tipo de áudio", example = "Sistema de som imersivo Dolby Atmos") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "10.00") BigDecimal additionalValue,

        @Schema(description = "Data de criação") Instant createdAt) {
}
