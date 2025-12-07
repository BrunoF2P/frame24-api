package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de um assento dentro do contexto de criação de sala (sem
 * roomId).
 */
@Schema(description = "Dados para criar um assento (nested)")
public record RoomSeatRequest(
        @NotBlank(message = "Código do assento é obrigatório") @Size(max = 10, message = "Código deve ter no máximo 10 caracteres") @Schema(description = "Código único do assento (ex: A1, B5)", example = "A1") String seatCode,

        @NotBlank(message = "Código da fileira é obrigatório") @Size(max = 5, message = "Código da fileira deve ter no máximo 5 caracteres") @Schema(description = "Identificador da fileira (ex: A, B)", example = "A") String rowCode,

        @NotNull(message = "Número da coluna é obrigatório") @Min(value = 1, message = "Número da coluna deve ser pelo menos 1") @Schema(description = "Número da coluna/assento na fileira", example = "1") Integer columnNumber,

        @Schema(description = "ID do tipo de assento", example = "1234567890123456789") Long seatTypeId,

        @Schema(description = "Posição X para renderização visual", example = "10") Integer positionX,

        @Schema(description = "Posição Y para renderização visual", example = "20") Integer positionY,

        @Schema(description = "Se o assento é acessível (para deficientes)", example = "false") Boolean accessible) {
}
