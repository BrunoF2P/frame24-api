package com.frame24.api.identity.application.dto;

import com.frame24.api.common.validation.ValidCnpj;
import com.frame24.api.identity.domain.enums.CompanyPlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Request para registro de nova empresa com administrador.
 */
@Schema(description = "Dados para registro de nova empresa e administrador")
public record CompanyRegistrationRequest(

        // ===== Dados da Empresa =====

        @Schema(description = "Razão social da empresa", example = "Cinema Estrela LTDA") @NotBlank(message = "Razão social é obrigatória") @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres") String corporateName,

        @Schema(description = "Nome fantasia", example = "CineEstrela") @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres") String tradeName,

        @Schema(description = "CNPJ da empresa (aceita com ou sem formatação)", example = "98.765.432/0001-10") @NotBlank(message = "CNPJ é obrigatório") @ValidCnpj String cnpj,

        @Schema(description = "CEP da empresa (aceita com ou sem traço)", example = "01310-100") @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX") String companyZipCode,

        @Schema(description = "Logradouro", example = "Av. Paulista") @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres") String companyStreetAddress,

        @Schema(description = "Número do endereço", example = "1578") @Size(max = 20, message = "Número deve ter no máximo 20 caracteres") String companyAddressNumber,

        @Schema(description = "Complemento", example = "Sala 501") @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres") String companyAddressComplement,

        @Schema(description = "Bairro", example = "Bela Vista") @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres") String companyNeighborhood,

        @Schema(description = "Cidade", example = "São Paulo") @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres") String companyCity,

        @Schema(description = "Estado - UF em maiúsculas", example = "SP") @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ter 2 letras maiúsculas") String companyState,

        @Schema(description = "Telefone da empresa (formato livre)", example = "(11) 3333-4444") @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres") String companyPhone,

        @Schema(description = "Email corporativo", example = "contato@cineestrela.com") @Email(message = "Email da empresa inválido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String companyEmail,

        // ===== Dados do Administrador =====

        @Schema(description = "Nome completo do administrador", example = "Maria Oliveira") @NotBlank(message = "Nome do administrador é obrigatório") @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres") String fullName,

        @Schema(description = "Email do administrador", example = "maria@cineestrela.com") @NotBlank(message = "Email do administrador é obrigatório") @Email(message = "Email do administrador inválido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String email,

        @Schema(description = "Senha do administrador", example = "SenhaSegura123") @NotBlank(message = "Senha é obrigatória") @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número") String password,

        @Schema(description = "Celular do administrador (formato livre)", example = "(11) 98765-4321") @Size(max = 20, message = "Celular deve ter no máximo 20 caracteres") String mobile,

        // ===== Plano =====

        @Schema(description = "Tipo do plano", example = "BASIC") @NotNull(message = "Tipo do plano é obrigatório") CompanyPlanType planType

) {
}
