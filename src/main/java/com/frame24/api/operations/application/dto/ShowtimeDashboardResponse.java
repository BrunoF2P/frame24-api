package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Dados resumidos da sessão para dashboard (via View)")
public record ShowtimeDashboardResponse(
        @Schema(description = "ID da sessão") Long showtimeId,

        @Schema(description = "ID do complexo") Long cinemaComplexId,

        @Schema(description = "ID da sala") Long roomId,

        @Schema(description = "Nome da sala") String roomName,

        @Schema(description = "ID do filme") Long movieId,

        @Schema(description = "Título do filme") String movieTitle,

        @Schema(description = "Horário de início") Instant startTime,

        @Schema(description = "Horário de término") Instant endTime,

        @Schema(description = "Total de assentos da sala") Long totalSeats,

        @Schema(description = "Assentos vendidos") Long seatsSold,

        @Schema(description = "Assentos disponíveis") Long seatsAvailable,

        @Schema(description = "Porcentagem de ocupação") BigDecimal occupancyPercentage,

        @Schema(description = "Status da sessão") String sessionStatus,

        @Schema(description = "Preço base") BigDecimal baseTicketPrice) {
}
