package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de sala de cinema.
 * Todos os campos são opcionais.
 */
@Schema(description = "Dados para atualizar uma sala de cinema")
public record UpdateRoomRequest(
        @Size(max = 10, message = "Número da sala deve ter no máximo 10 caracteres") @Schema(description = "Número/identificador da sala", example = "01A") String roomNumber,

        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome da sala", example = "Sala VIP IMAX") String name,

        @Min(value = 1, message = "Capacidade mínima é 1") @Schema(description = "Capacidade total de assentos", example = "160") Integer capacity,

        @Schema(description = "ID do tipo de projeção", example = "1234567890123456789") Long projectionTypeId,

        @Schema(description = "ID do tipo de áudio", example = "1234567890123456789") Long audioTypeId,

        @Schema(description = "Se a sala está ativa", example = "true") Boolean active,

        @Schema(description = "Número total de fileiras", example = "12") Integer totalRows,

        @Schema(description = "Número total de colunas", example = "16") Integer totalColumns,

        @Size(max = 30, message = "Design deve ter no máximo 30 caracteres") @Schema(description = "Design da sala (STADIUM, FLAT, etc)", example = "STADIUM") String roomDesign) {
}
