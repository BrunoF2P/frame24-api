package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO de response para sala de cinema.
 */
@Schema(description = "Dados de uma sala de cinema")
public record RoomResponse(
        @Schema(description = "ID da sala", example = "1234567890123456789") Long id,

        @Schema(description = "ID do complexo de cinema", example = "1234567890123456789") Long cinemaComplexId,

        @Schema(description = "Nome do complexo de cinema", example = "Cineplex Shopping Center") String cinemaComplexName,

        @Schema(description = "Número/identificador da sala", example = "01") String roomNumber,

        @Schema(description = "Nome da sala", example = "Sala Premium IMAX") String name,

        @Schema(description = "Capacidade total de assentos", example = "150") Integer capacity,

        @Schema(description = "Informações do tipo de projeção") TypeInfo projectionType,

        @Schema(description = "Informações do tipo de áudio") TypeInfo audioType,

        @Schema(description = "Se a sala está ativa", example = "true") Boolean active,

        @Schema(description = "Número total de fileiras", example = "10") Integer totalRows,

        @Schema(description = "Número total de colunas", example = "15") Integer totalColumns,

        @Schema(description = "Design da sala", example = "STADIUM") String roomDesign,

        @Schema(description = "Layout visual dos assentos (JSON)") Object seatLayout,

        @Schema(description = "Data de criação") Instant createdAt) {
    /**
     * Informações resumidas de tipo (projeção ou áudio).
     */
    @Schema(description = "Informações de tipo")
    public record TypeInfo(
            @Schema(description = "ID do tipo", example = "1234567890123456789") Long id,

            @Schema(description = "Nome do tipo", example = "IMAX") String name) {
    }
}
