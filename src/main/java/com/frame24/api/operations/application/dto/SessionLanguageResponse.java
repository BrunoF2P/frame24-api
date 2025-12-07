package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Dados de um idioma de sessão")
public record SessionLanguageResponse(
        @Schema(description = "ID do idioma", example = "1234567890123456789") Long id,

        @Schema(description = "Nome do idioma", example = "Dubbed") String name,

        @Schema(description = "Descrição do idioma", example = "Áudio dublado em português") String description,

        @Schema(description = "Abreviação do idioma", example = "DUB") String abbreviation,

        @Schema(description = "Data de criação") Instant createdAt) {
}
