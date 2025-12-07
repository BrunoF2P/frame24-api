package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Request para busca de usuários com filtros.
 */
@Schema(description = "Critérios de busca para usuários")
public record SearchUserRequest(

        @Schema(description = "ID do funcionário (busca exata)", example = "1001") Long employeeId,

        @Schema(description = "Email (busca parcial)", example = "joao") String email,

        @Schema(description = "Nome completo (busca parcial)", example = "Silva") String fullName,

        @Schema(description = "Departamento (busca exata)", example = "Operações") String department,

        @Schema(description = "Status ativo", example = "true") Boolean active,

        @Schema(description = "ID da role", example = "123456789") Long roleId,

        // ===== Paginação =====

        @Schema(description = "Número da página (zero-indexed)", example = "0") @Min(value = 0, message = "Página deve ser maior ou igual a 0") Integer page,

        @Schema(description = "Tamanho da página", example = "20") @Min(value = 1, message = "Tamanho da página deve ser maior que 0") Integer size,

        @Schema(description = "Campo para ordenação", example = "fullName") String sortBy,

        @Schema(description = "Direção da ordenação (ASC ou DESC)", example = "ASC") String sortDirection

) {
    /**
     * Retorna página com valor padrão se não especificado.
     */
    public int getPageOrDefault() {
        return page != null ? page : 0;
    }

    /**
     * Retorna tamanho com valor padrão se não especificado.
     */
    public int getSizeOrDefault() {
        return size != null ? size : 20;
    }

    /**
     * Retorna campo de ordenação com valor padrão se não especificado.
     */
    public String getSortByOrDefault() {
        return sortBy != null && !sortBy.isBlank() ? sortBy : "fullName";
    }

    /**
     * Retorna direção da ordenação com valor padrão se não especificado.
     */
    public String getSortDirectionOrDefault() {
        return sortDirection != null && sortDirection.equalsIgnoreCase("DESC") ? "DESC" : "ASC";
    }
}
