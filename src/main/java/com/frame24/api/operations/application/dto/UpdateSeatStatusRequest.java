package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de status de assento.
 * Todos os campos são opcionais.
 */
@Schema(description = "Dados para atualizar um status de assento")
public record UpdateSeatStatusRequest(
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do status", example = "Maintenance") String name,

        @Schema(description = "Descrição do status", example = "Assento em manutenção") String description,

        @Schema(description = "Se o status permite que o assento seja modificado/selecionado", example = "false") Boolean allowsModification,

        @Schema(description = "Se este é o status padrão para novos assentos", example = "false") Boolean isDefault) {
}
