package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;

/**
 * Response com dados de uma role.
 */
@Schema(description = "Dados de uma role")
public record RoleResponse(

        @Schema(description = "ID da role", example = "1234567890123456789") Long id,

        @Schema(description = "ID da empresa", example = "9876543210987654321") Long companyId,

        @Schema(description = "Nome da role", example = "Gerente de Vendas") String name,

        @Schema(description = "Descrição da role", example = "Gerencia operações de vendas e equipe comercial") String description,

        @Schema(description = "Indica se é uma role do sistema (não pode ser deletada)", example = "false") Boolean isSystemRole,

        @Schema(description = "Nível hierárquico da role", example = "4") Integer hierarchyLevel,

        @Schema(description = "Data de criação", example = "2024-01-15T10:30:00Z") Instant createdAt,

        @Schema(description = "Data de atualização", example = "2024-01-20T14:45:00Z") Instant updatedAt,

        @Schema(description = "Lista de códigos de permissão associados à role", example = "[\"users:read\", \"sales:create\"]") Set<String> permissions

) {
}
