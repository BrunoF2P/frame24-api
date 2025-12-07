package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response com dados de uma permissão.
 */
@Schema(description = "Dados de uma permissão do sistema")
public record PermissionResponse(

        @Schema(description = "ID da permissão", example = "1234567890123456789") Long id,

        @Schema(description = "Recurso (entidade) ao qual a permissão se aplica", example = "users") String resource,

        @Schema(description = "Ação permitida", example = "read") String action,

        @Schema(description = "Código único da permissão (usado para vincular à role)", example = "users:read") String code,

        @Schema(description = "Nome legível da permissão", example = "Visualizar Usuários") String name,

        @Schema(description = "Descrição detalhada", example = "Permite visualizar lista de usuários") String description,

        @Schema(description = "Módulo do sistema", example = "identity") String module

) {
}
