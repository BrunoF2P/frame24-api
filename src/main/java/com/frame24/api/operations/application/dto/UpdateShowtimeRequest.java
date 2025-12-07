package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Dados para atualizar uma sessão (showtime)")
public record UpdateShowtimeRequest(
        @Schema(description = "Horário de início da sessão (ISO 8601)", example = "2023-12-25T15:00:00Z") Instant startTime,

        @Schema(description = "Horário de término da sessão (ISO 8601)", example = "2023-12-25T17:30:00Z") Instant endTime,

        @Schema(description = "ID do tipo de áudio", example = "123") Long audioTypeId,

        @Schema(description = "ID do tipo de projeção", example = "456") Long projectionTypeId,

        @Schema(description = "ID do idioma da sessão", example = "789") Long sessionLanguageId,

        @Schema(description = "ID do status da sessão", example = "12") Long statusId,

        @DecimalMin(value = "0.00", message = "Preço base deve ser maior ou igual a zero") @Schema(description = "Preço base do ingresso para esta sessão", example = "28.00") BigDecimal baseTicketPrice) {
}
