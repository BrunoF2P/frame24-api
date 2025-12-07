package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de status de assento.
 */
@Schema(description = "Dados para criar um status de assento")
public record CreateSeatStatusRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do status", example = "Blocked") String name,

        @Schema(description = "Descrição do status", example = "Assento bloqueado para manutenção") String description,

        @NotNull(message = "Permite modificação é obrigatório") @Schema(description = "Se o status permite que o assento seja modificado/selecionado", example = "false") Boolean allowsModification,

        @Schema(description = "Se este é o status padrão para novos assentos", example = "false", defaultValue = "false") Boolean isDefault) {
}
