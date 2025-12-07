package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO de response para status de assento.
 */
@Schema(description = "Dados de um status de assento")
public record SeatStatusResponse(
        @Schema(description = "ID do status", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do status", example = "Available") String name,

        @Schema(description = "Descrição do status", example = "Assento disponível") String description,

        @Schema(description = "Se o status permite que o assento seja modificado/selecionado", example = "true") Boolean allowsModification,

        @Schema(description = "Se este é o status padrão", example = "true") Boolean isDefault,

        @Schema(description = "Data de criação") Instant createdAt) {
}
