package com.frame24.api.identity.application.dto;

import com.frame24.api.common.validation.ValidCpf;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Request para atualização de usuário existente.
 * Todos os campos são opcionais exceto roleId.
 */
@Schema(description = "Dados para atualização de usuário existente")
public record UpdateUserRequest(

        // ===== Dados Pessoais =====

        @Schema(description = "Nome completo do usuário", example = "João da Silva") @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres") String fullName,

        @Schema(description = "Email do usuário", example = "joao.silva@empresa.com") @Email(message = "Email inválido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String email,

        @Schema(description = "Nova senha (requer maiúscula, minúscula e número)", example = "NovaSenha123") @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número") String password,

        @Schema(description = "CPF do usuário (aceita com ou sem formatação)", example = "123.456.789-00") @ValidCpf String cpf,

        @Schema(description = "Data de nascimento", example = "1990-05-15") @Past(message = "Data de nascimento deve ser no passado") LocalDate birthDate,

        @Schema(description = "Telefone (formato livre)", example = "(11) 3333-4444") @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres") String phone,

        @Schema(description = "Celular (formato livre)", example = "(11) 98765-4321") @Size(max = 20, message = "Celular deve ter no máximo 20 caracteres") String mobile,

        // ===== Endereço =====

        @Schema(description = "CEP (aceita com ou sem traço)", example = "01310-100") @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX") String zipCode,

        @Schema(description = "Logradouro", example = "Av. Paulista") @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres") String streetAddress,

        @Schema(description = "Número do endereço", example = "1578") @Size(max = 20, message = "Número deve ter no máximo 20 caracteres") String addressNumber,

        @Schema(description = "Complemento", example = "Apto 501") @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres") String addressComplement,

        @Schema(description = "Bairro", example = "Bela Vista") @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres") String neighborhood,

        @Schema(description = "Cidade", example = "São Paulo") @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres") String city,

        @Schema(description = "Estado - UF em maiúsculas", example = "SP") @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ter 2 letras maiúsculas") String state,

        @Schema(description = "País - Código ISO em maiúsculas", example = "BR") @Pattern(regexp = "^[A-Z]{2}$", message = "País deve ter 2 letras maiúsculas") String country,

        // ===== Dados do Usuário na Empresa =====

        @Schema(description = "ID da role/função do usuário", example = "123456789") Long roleId,

        @Schema(description = "Status ativo do usuário", example = "true") Boolean active,

        @Schema(description = "Lista de IDs dos complexos que o usuário pode acessar", example = "[1, 2, 3]") List<Long> allowedComplexes,

        @Schema(description = "Departamento (opcional - para gestão de RH)", example = "Operações") @Size(max = 100, message = "Departamento deve ter no máximo 100 caracteres") String department,

        @Schema(description = "Nível do cargo (opcional - para gestão de RH)", example = "Gerente") @Size(max = 50, message = "Nível do cargo deve ter no máximo 50 caracteres") String jobLevel,

        @Schema(description = "Localização/Unidade de trabalho (opcional)", example = "Shopping Paulista") @Size(max = 100, message = "Localização deve ter no máximo 100 caracteres") String location,

        @Schema(description = "Data de término", example = "2024-12-31") LocalDate endDate

) {
}
