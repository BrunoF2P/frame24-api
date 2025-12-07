package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Dados de um status de sessão")
public record SessionStatusResponse(
        @Schema(description = "ID do status", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do status", example = "Selling") String name,

        @Schema(description = "Descrição do status", example = "Sessão aberta para vendas") String description,

        @Schema(description = "Se o status permite modificação", example = "true") Boolean allowsModification,

        @Schema(description = "Data de criação") Instant createdAt) {
}
