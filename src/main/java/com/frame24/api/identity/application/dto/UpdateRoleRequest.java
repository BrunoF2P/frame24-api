package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request para atualização de role customizada.
 */
@Schema(description = "Dados para atualização de uma role customizada")
public record UpdateRoleRequest(

        @Schema(description = "Nome da role", example = "Gerente de Vendas") @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") String name,

        @Schema(description = "Descrição da role", example = "Gerencia operações de vendas e equipe comercial") @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres") String description,

        @Schema(description = "Nível hierárquico da role (maior número = menor privilégio)", example = "4") Integer hierarchyLevel,

        @Schema(description = "Lista de códigos de permissão associados à role", example = "[\"users:read\", \"sales:create\"]") Set<String> permissions

) {
}
