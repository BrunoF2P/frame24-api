package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response do login contendo tokens JWT e informações do usuário.
 */
@Schema(description = "Resposta de autenticação bem-sucedida")
public record LoginResponse(

        @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,

        @Schema(description = "Token de refresh para renovar o access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String refreshToken,

        @Schema(description = "Tipo do token", example = "Bearer") String tokenType,

        @Schema(description = "Tempo de expiração do access token em segundos", example = "3600") Long expiresIn,

        @Schema(description = "Informações do usuário autenticado") UserInfo user

) {
    /**
     * Informações do usuário autenticado.
     */
    @Schema(description = "Dados do usuário")
    public record UserInfo(
            @Schema(description = "ID da identity", example = "1234567890123456789") Long id,

            @Schema(description = "Nome completo", example = "Maria Oliveira") String fullName,

            @Schema(description = "Email", example = "maria@cineestrela.com") String email,

            @Schema(description = "ID da empresa", example = "9876543210987654321") Long companyId,

            @Schema(description = "Nome da empresa", example = "Cinema Estrela LTDA") String companyName,

            @Schema(description = "Slug do tenant", example = "cineestrela") String tenantSlug,

            @Schema(description = "Nome do role/função", example = "Administrador") String roleName,

            @Schema(description = "Tipo de identidade", example = "EMPLOYEE") String identityType) {
    }
}
