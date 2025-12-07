package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de assento.
 */
@Schema(description = "Dados para atualizar um assento")
public record UpdateSeatRequest(
        @Size(max = 10, message = "Código deve ter no máximo 10 caracteres") @Schema(description = "Código único do assento", example = "A1-VIP") String seatCode,

        @Size(max = 5, message = "Código da fileira deve ter no máximo 5 caracteres") @Schema(description = "Identificador da fileira", example = "A") String rowCode,

        @Schema(description = "Número da coluna", example = "1") Integer columnNumber,

        @Schema(description = "ID do tipo de assento", example = "1234567890123456789") Long seatTypeId,

        @Schema(description = "Posição X", example = "10") Integer positionX,

        @Schema(description = "Posição Y", example = "20") Integer positionY,

        @Schema(description = "Acessível", example = "true") Boolean accessible,

        @Schema(description = "Ativo", example = "true") Boolean active) {
}
