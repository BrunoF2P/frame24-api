package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para criação de tipo de projeção.
 */
@Schema(description = "Dados para criar um tipo de projeção")
public record CreateProjectionTypeRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome do tipo de projeção", example = "IMAX") String name,

        @Schema(description = "Descrição do tipo de projeção", example = "Projeção em tela gigante IMAX") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "15.00") BigDecimal additionalValue) {
}
