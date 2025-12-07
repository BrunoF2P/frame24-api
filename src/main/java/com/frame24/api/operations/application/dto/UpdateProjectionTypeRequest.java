package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para atualização de tipo de projeção.
 * Todos os campos são opcionais.
 */
@Schema(description = "Dados para atualizar um tipo de projeção")
public record UpdateProjectionTypeRequest(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome do tipo de projeção", example = "IMAX Laser") String name,

        @Schema(description = "Descrição do tipo de projeção", example = "Projeção IMAX com tecnologia laser") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "20.00") BigDecimal additionalValue) {
}
