package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualizar um idioma de sessão")
public record UpdateSessionLanguageRequest(
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do idioma", example = "Original") String name,

        @Schema(description = "Descrição do idioma", example = "Áudio original com legendas") String description,

        @Size(max = 10, message = "Abreviação deve ter no máximo 10 caracteres") @Schema(description = "Abreviação do idioma", example = "LEG") String abbreviation) {
}
