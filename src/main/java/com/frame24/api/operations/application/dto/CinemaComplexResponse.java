package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Response com dados de um complexo de cinema.
 */
@Schema(description = "Dados de um complexo de cinema")
public record CinemaComplexResponse(

        @Schema(description = "ID único do complexo", example = "1234567890123456789") Long id,

        @Schema(description = "ID da empresa", example = "9876543210987654321") Long companyId,

        @Schema(description = "Nome do complexo de cinema", example = "Cinemark Shopping Center") String name,

        @Schema(description = "Código único do complexo", example = "CMRK-001") String code,

        @Schema(description = "CNPJ do complexo", example = "12.345.678/0001-99") String cnpj,

        @Schema(description = "Endereço completo", example = "Av. Paulista, 1000") String address,

        @Schema(description = "Cidade", example = "São Paulo") String city,

        @Schema(description = "Estado (UF)", example = "SP") String state,

        @Schema(description = "CEP", example = "01310-100") String postalCode,

        @Schema(description = "Código IBGE do município", example = "3550308") String ibgeMunicipalityCode,

        @Schema(description = "Registro na ANCINE", example = "REG-2024-001") String ancineRegistry,

        @Schema(description = "Data de abertura do complexo", example = "2024-01-15") LocalDate openingDate,

        @Schema(description = "Status ativo", example = "true") Boolean active,

        @Schema(description = "Data de criação do registro", example = "2024-01-01T10:00:00Z") Instant createdAt,

        @Schema(description = "Data da última atualização", example = "2024-01-15T14:30:00Z") Instant updatedAt

) {
}
