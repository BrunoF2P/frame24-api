package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Dados para criar uma sessão (showtime)")
public record CreateShowtimeRequest(
        @NotNull(message = "ID da sala é obrigatório") @Schema(description = "ID da sala", example = "1234567890123456789") Long roomId,

        @NotNull(message = "ID do filme é obrigatório") @Schema(description = "ID do filme", example = "101") Long movieId,

        @NotNull(message = "Horário de início é obrigatório") @Schema(description = "Horário de início da sessão (ISO 8601)", example = "2023-12-25T14:30:00Z") Instant startTime,

        @NotNull(message = "Horário de término é obrigatório") @Schema(description = "Horário de término da sessão (ISO 8601)", example = "2023-12-25T17:00:00Z") Instant endTime,

        @NotNull(message = "ID do tipo de áudio é obrigatório") @Schema(description = "ID do tipo de áudio", example = "123") Long audioTypeId,

        @NotNull(message = "ID do tipo de projeção é obrigatório") @Schema(description = "ID do tipo de projeção", example = "456") Long projectionTypeId,

        @NotNull(message = "ID do idioma da sessão é obrigatório") @Schema(description = "ID do idioma da sessão", example = "789") Long sessionLanguageId,

        @NotNull(message = "ID do status da sessão é obrigatório") @Schema(description = "ID do status da sessão", example = "12") Long statusId,

        @NotNull(message = "Preço base do ingresso é obrigatório") @DecimalMin(value = "0.00", message = "Preço base deve ser maior ou igual a zero") @Schema(description = "Preço base do ingresso para esta sessão", example = "25.00") BigDecimal baseTicketPrice) {
}
