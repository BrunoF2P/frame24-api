package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Preview financeiro de uma sessão")
public record FinancialPreviewResponse(
        @Schema(description = "ID da sessão") Long showtimeId,

        @Schema(description = "Total de assentos da sala") Long totalSeats,

        @Schema(description = "Assentos vendidos") Long seatsSold,

        @Schema(description = "Assentos disponíveis") Long seatsAvailable,

        @Schema(description = "Porcentagem de ocupação") BigDecimal occupancyPercentage,

        @Schema(description = "Preço base do ingresso") BigDecimal baseTicketPrice,

        @Schema(description = "Receita estimada (assentos vendidos * preço base)") BigDecimal estimatedRevenue) {
}
