package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para criação de sala de cinema.
 */
@Schema(description = "Dados para criar uma sala de cinema")
public record CreateRoomRequest(
        @NotNull(message = "ID do complexo é obrigatório") @Schema(description = "ID do complexo de cinema", example = "1234567890123456789") Long cinemaComplexId,

        @NotBlank(message = "Número da sala é obrigatório") @Size(max = 10, message = "Número da sala deve ter no máximo 10 caracteres") @Schema(description = "Número/identificador da sala", example = "01") String roomNumber,

        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome da sala", example = "Sala Premium IMAX") String name,

        @NotNull(message = "Capacidade é obrigatória") @Min(value = 1, message = "Capacidade mínima é 1") @Schema(description = "Capacidade total de assentos", example = "150") Integer capacity,

        @Schema(description = "ID do tipo de projeção", example = "1234567890123456789") Long projectionTypeId,

        @Schema(description = "ID do tipo de áudio", example = "1234567890123456789") Long audioTypeId,

        @Schema(description = "Número total de fileiras", example = "10") Integer totalRows,

        @Schema(description = "Número total de colunas", example = "15") Integer totalColumns,

        @Size(max = 30, message = "Design deve ter no máximo 30 caracteres") @Schema(description = "Design da sala (STADIUM, FLAT, etc)", example = "STADIUM") String roomDesign,

        @Schema(description = "Lista de assentos para criar junto com a sala") List<RoomSeatRequest> seats) {
}
