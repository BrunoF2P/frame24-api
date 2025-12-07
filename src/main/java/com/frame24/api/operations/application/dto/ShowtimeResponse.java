package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Dados de uma sessão (showtime)")
public record ShowtimeResponse(
        @Schema(description = "ID da sessão", example = "1234567890123456789") Long id,

        @Schema(description = "ID do complexo de cinema", example = "100") Long cinemaComplexId,

        @Schema(description = "Nome do complexo de cinema", example = "Cineplex Downtown") String cinemaComplexName,

        @Schema(description = "ID da sala", example = "200") Long roomId,

        @Schema(description = "Nome/Número da sala", example = "Sala 1") String roomName,

        @Schema(description = "ID do filme", example = "101") Long movieId,

        @Schema(description = "Horário de início", example = "2023-12-25T14:30:00Z") Instant startTime,

        @Schema(description = "Horário de término", example = "2023-12-25T17:00:00Z") Instant endTime,

        @Schema(description = "Tipo de áudio") TypeInfo audioType,

        @Schema(description = "Tipo de projeção") TypeInfo projectionType,

        @Schema(description = "Idioma da sessão") LanguageInfo sessionLanguage,

        @Schema(description = "Status da sessão") StatusInfo status,

        @Schema(description = "Assentos disponíveis", example = "150") Integer availableSeats,

        @Schema(description = "Preço base do ingresso", example = "25.00") BigDecimal baseTicketPrice,

        @Schema(description = "Data de criação") Instant createdAt) {
    public record TypeInfo(Long id, String name) {
    }

    public record LanguageInfo(Long id, String name, String abbreviation) {
    }

    public record StatusInfo(Long id, String name, Boolean allowsModification) {
    }
}
