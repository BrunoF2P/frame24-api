package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response com lista paginada de usuários.
 */
@Schema(description = "Lista paginada de usuários")
public record UserListResponse(

        @Schema(description = "Lista de usuários") List<UserResponse> users,

        @Schema(description = "Total de registros", example = "150") Long totalCount,

        @Schema(description = "Tamanho da página", example = "20") Integer pageSize,

        @Schema(description = "Página atual (zero-indexed)", example = "0") Integer currentPage,

        @Schema(description = "Total de páginas", example = "8") Integer totalPages

) {
}
