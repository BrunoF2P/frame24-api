package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response com dados completos do usuário.
 * Nunca expõe dados sensíveis como senha.
 */
@Schema(description = "Dados completos do usuário")
public record UserResponse(

        // ===== Identificação =====

        @Schema(description = "ID do usuário", example = "123456789") Long id,

        @Schema(description = "ID do funcionário (número sequencial)", example = "1001") Long employeeId,

        @Schema(description = "ID da identity", example = "987654321") Long identityId,

        // ===== Dados Pessoais =====

        @Schema(description = "Nome completo", example = "João da Silva") String fullName,

        @Schema(description = "Email", example = "joao.silva@empresa.com") String email,

        @Schema(description = "CPF", example = "123.456.789-00") String cpf,

        @Schema(description = "Data de nascimento", example = "1990-05-15") LocalDate birthDate,

        @Schema(description = "Telefone", example = "(11) 3333-4444") String phone,

        @Schema(description = "Celular", example = "(11) 98765-4321") String mobile,

        // ===== Endereço =====

        @Schema(description = "CEP", example = "01310-100") String zipCode,

        @Schema(description = "Logradouro", example = "Av. Paulista") String streetAddress,

        @Schema(description = "Número", example = "1578") String addressNumber,

        @Schema(description = "Complemento", example = "Apto 501") String addressComplement,

        @Schema(description = "Bairro", example = "Bela Vista") String neighborhood,

        @Schema(description = "Cidade", example = "São Paulo") String city,

        @Schema(description = "Estado", example = "SP") String state,

        @Schema(description = "País", example = "BR") String country,

        // ===== Dados Organizacionais =====

        @Schema(description = "Informações da role") RoleInfo role,

        @Schema(description = "Departamento", example = "Operações") String department,

        @Schema(description = "Nível do cargo", example = "Gerente") String jobLevel,

        @Schema(description = "Localização", example = "São Paulo - Shopping Paulista") String location,

        @Schema(description = "Complexos permitidos") List<ComplexInfo> allowedComplexes,

        @Schema(description = "Status ativo", example = "true") Boolean active,

        @Schema(description = "Data de início", example = "2024-01-15") LocalDate startDate,

        @Schema(description = "Data de término", example = "2024-12-31") LocalDate endDate,

        // ===== Auditoria =====

        @Schema(description = "Criado por", example = "admin@empresa.com") String assignedBy,

        @Schema(description = "Data de criação") Instant createdAt,

        @Schema(description = "Data de atualização") Instant updatedAt,

        @Schema(description = "Último acesso") Instant lastAccess,

        @Schema(description = "Contagem de acessos", example = "42") Integer accessCount

) {
    /**
     * Informações resumidas da role.
     */
    public record RoleInfo(
            @Schema(description = "ID da role", example = "123456789") Long id,

            @Schema(description = "Nome da role", example = "Gerente de Operações") String name,

            @Schema(description = "Nível hierárquico", example = "3") Integer hierarchyLevel) {
    }

    /**
     * Informações resumidas do complexo.
     */
    public record ComplexInfo(
            @Schema(description = "ID do complexo", example = "123456789") Long id,

            @Schema(description = "Nome do complexo", example = "CineStar Shopping Paulista") String name,

            @Schema(description = "Código do complexo", example = "SP-001") String code) {
    }
}
