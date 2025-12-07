package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO de response para assento.
 */
@Schema(description = "Dados de um assento")
public record SeatResponse(
        @Schema(description = "ID do assento", example = "1234567890123456789") Long id,

        @Schema(description = "ID da sala", example = "1234567890123456789") Long roomId,

        @Schema(description = "Código do assento", example = "A1") String seatCode,

        @Schema(description = "Código da fileira", example = "A") String rowCode,

        @Schema(description = "Número da coluna", example = "1") Integer columnNumber,

        @Schema(description = "Informações do tipo de assento") TypeInfo seatType,

        @Schema(description = "Posição X visual", example = "10") Integer positionX,

        @Schema(description = "Posição Y visual", example = "20") Integer positionY,

        @Schema(description = "Se é acessível", example = "false") Boolean accessible,

        @Schema(description = "Se está ativo", example = "true") Boolean active,

        @Schema(description = "Data de criação") Instant createdAt) {
    public record TypeInfo(
            Long id,
            String name,
            String description) {
    }
}
