package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para atualização de tipo de áudio.
 * Todos os campos são opcionais.
 */
@Schema(description = "Dados para atualizar um tipo de áudio")
public record UpdateAudioTypeRequest(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome do tipo de áudio", example = "Dolby Atmos 7.1") String name,

        @Schema(description = "Descrição do tipo de áudio", example = "Sistema Dolby Atmos 7.1 canais") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "12.00") BigDecimal additionalValue) {
}
