package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para criação de tipo de assento.
 */
@Schema(description = "Dados para criar um tipo de assento")
public record CreateSeatTypeRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") @Schema(description = "Nome do tipo de assento", example = "VIP") String name,

        @Schema(description = "Descrição do tipo de assento", example = "Assento reclinável de couro") String description,

        @Schema(description = "Valor adicional cobrado por este tipo", example = "10.00") BigDecimal additionalValue) {
}
