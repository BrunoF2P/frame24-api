package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar um idioma de sessão")
public record CreateSessionLanguageRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do idioma", example = "Dubbed") String name,

        @Schema(description = "Descrição do idioma", example = "Áudio dublado em português") String description,

        @Size(max = 10, message = "Abreviação deve ter no máximo 10 caracteres") @Schema(description = "Abreviação do idioma", example = "DUB") String abbreviation) {
}
