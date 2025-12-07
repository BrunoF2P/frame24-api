package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dados para atualização de status de assentos em lote (Manutenção)")
public record BatchUpdateSeatStatusRequest(
        @NotNull(message = "ID da sala é obrigatório") @Schema(description = "ID da sala", example = "123456789") Long roomId,

        @NotEmpty(message = "Lista de IDs de assentos não pode ser vazia") @Schema(description = "Lista de IDs dos assentos a atualizar", example = "[1, 2, 3]") List<Long> seatIds,

        @NotNull(message = "Status ativo é obrigatório") @Schema(description = "Novo status (true=ativo, false=inativo/manutenção)", example = "false") Boolean active) {
}
