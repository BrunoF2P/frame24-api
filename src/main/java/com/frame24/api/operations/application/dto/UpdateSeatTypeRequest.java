package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para atualização de tipo de assento.
 * Todos os campos são opcionais.
 */
@Schema(description = "Dados para atualizar um tipo de assento")
public record UpdateSeatTypeRequest(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome do tipo de assento", example = "Premium") String name,

        @Schema(description = "Descrição do tipo de assento", example = "Assento super confortável") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "15.00") BigDecimal additionalValue) {
}
