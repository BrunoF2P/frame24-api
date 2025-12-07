package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualizar um status de sessão")
public record UpdateSessionStatusRequest(
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres") @Schema(description = "Nome do status", example = "Closed") String name,

        @Schema(description = "Descrição do status", example = "Sessão encerrada") String description,

        @Schema(description = "Se o status permite que a sessão seja modificada", example = "false") Boolean allowsModification) {
}
