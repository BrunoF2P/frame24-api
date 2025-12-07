package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar um status de sessão")
public record CreateSessionStatusRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do status", example = "Selling") String name,

        @Schema(description = "Descrição do status", example = "Sessão aberta para vendas") String description,

        @NotNull(message = "Permite modificação é obrigatório") @Schema(description = "Se o status permite que a sessão seja modificada", example = "true") Boolean allowsModification) {
}
