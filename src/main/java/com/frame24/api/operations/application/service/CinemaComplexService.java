package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.BusinessException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CinemaComplexResponse;
import com.frame24.api.operations.application.dto.CreateCinemaComplexRequest;
import com.frame24.api.operations.application.dto.UpdateCinemaComplexRequest;
import com.frame24.api.operations.domain.CinemaComplex;
import com.frame24.api.operations.infrastructure.repository.CinemaComplexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de complexos de cinema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaComplexService {

    private final CinemaComplexRepository cinemaComplexRepository;

    /**
     * Cria um novo complexo de cinema.
     */
    @Transactional
    public CinemaComplexResponse createCinemaComplex(CreateCinemaComplexRequest request, UserPrincipal principal) {
        log.info("Criando complexo de cinema para empresa: {}", principal.getCompanyId());

        // Gerar código automaticamente se não fornecido
        String code = request.code();
        if (code == null || code.isBlank()) {
            code = generateUniqueCode(principal.getCompanyId(), request.name());
            log.info("Código gerado automaticamente: {}", code);
        } else {
            // Validar se o código já existe para esta empresa
            if (cinemaComplexRepository.existsByCompanyIdAndCode(principal.getCompanyId(), code)) {
                throw new BusinessException("Já existe um complexo de cinema com o código: " + code);
            }
        }

        // Validar se o CNPJ já existe (se fornecido)
        String sanitizedCnpj = sanitizeNumericField(request.cnpj());
        if (sanitizedCnpj != null && cinemaComplexRepository.existsByCnpj(sanitizedCnpj)) {
            throw new BusinessException("Já existe um complexo de cinema com o CNPJ: " + request.cnpj());
        }

        // Limpar CEP removendo caracteres não numéricos
        String sanitizedPostalCode = sanitizeNumericField(request.postalCode());

        CinemaComplex cinemaComplex = new CinemaComplex();
        cinemaComplex.setCompanyId(principal.getCompanyId());
        cinemaComplex.setName(request.name());
        cinemaComplex.setCode(code);
        cinemaComplex.setCnpj(sanitizedCnpj);
        cinemaComplex.setAddress(request.address());
        cinemaComplex.setCity(request.city());
        cinemaComplex.setState(request.state());
        cinemaComplex.setPostalCode(sanitizedPostalCode);
        cinemaComplex.setIbgeMunicipalityCode(request.ibgeMunicipalityCode());
        cinemaComplex.setAncineRegistry(request.ancineRegistry());
        cinemaComplex.setOpeningDate(request.openingDate());
        cinemaComplex.setActive(request.active());
        cinemaComplex.setCreatedAt(Instant.now());

        CinemaComplex saved = cinemaComplexRepository.save(cinemaComplex);
        log.info("Complexo de cinema criado com sucesso: ID {} com código {}", saved.getId(), saved.getCode());

        return toResponse(saved);
    }

    /**
     * Remove caracteres não numéricos de campos como CPF, CNPJ e CEP.
     */
    private String sanitizeNumericField(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }

    /**
     * Gera um código único para o complexo de cinema baseado no nome.
     * Exemplos:
     * - "Cinemark Shopping Center" → CSC-001
     * - "UCI Kinoplex" → UK-001
     * - "Cinépolis" → C-001
     */
    private String generateUniqueCode(Long companyId, String name) {
        // Extrair prefixo do nome (iniciais das palavras)
        String prefix = extractInitials(name);

        int counter = 1;
        String code;

        // Buscar códigos existentes com o mesmo prefixo
        List<CinemaComplex> existingComplexes = cinemaComplexRepository.findByCompanyId(companyId);

        if (!existingComplexes.isEmpty()) {
            // Extrair os números dos códigos existentes com o mesmo prefixo e pegar o maior
            int maxNumber = existingComplexes.stream()
                    .map(CinemaComplex::getCode)
                    .filter(c -> c != null && c.startsWith(prefix + "-"))
                    .map(c -> c.substring(prefix.length() + 1)) // Remove "PREFIX-"
                    .filter(this::isNumeric)
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);

            counter = maxNumber + 1;
        }

        // Gerar código com padding de zeros (ex: CSC-001, CSC-002, etc.)
        code = prefix + "-" + String.format("%03d", counter);

        // Garantir que o código é único (segurança extra)
        while (cinemaComplexRepository.existsByCompanyIdAndCode(companyId, code)) {
            counter++;
            code = prefix + "-" + String.format("%03d", counter);
        }

        return code;
    }

    /**
     * Extrai as iniciais do nome para formar o prefixo do código.
     * Regras:
     * - Pega a primeira letra de cada palavra significativa
     * - Ignora preposições e artigos comuns (de, da, do, e, &, etc.)
     * - Converte para maiúsculas
     * - Máximo de 4 letras
     */
    private String extractInitials(String name) {
        if (name == null || name.isBlank()) {
            return "CX"; // Fallback padrão
        }

        // Palavras a ignorar (preposições, artigos, conectores)
        String[] ignoredWords = {"de", "da", "do", "dos", "das", "e", "&", "o", "a", "os", "as"};

        // Limpar e dividir o nome em palavras
        String[] words = name.trim()
                .replaceAll("[^a-zA-ZÀ-ÿ\\s]", "") // Remove caracteres especiais exceto letras e espaços
                .split("\\s+");

        StringBuilder initials = new StringBuilder();

        for (String word : words) {
            if (word.length() == 0)
                continue;

            // Verificar se não é uma palavra ignorada
            boolean shouldIgnore = false;
            for (String ignored : ignoredWords) {
                if (word.equalsIgnoreCase(ignored)) {
                    shouldIgnore = true;
                    break;
                }
            }

            if (!shouldIgnore) {
                initials.append(word.substring(0, 1).toUpperCase());

                // Limitar a 4 letras
                if (initials.length() >= 4) {
                    break;
                }
            }
        }

        // Se não conseguiu extrair nenhuma inicial (improvável), usar fallback
        if (initials.length() == 0) {
            return "CX";
        }

        return initials.toString();
    }

    /**
     * Verifica se uma string é numérica.
     */
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Lista todos os complexos de cinema da empresa.
     */
    @Transactional(readOnly = true)
    public List<CinemaComplexResponse> listCinemaComplexes(UserPrincipal principal) {
        log.info("Listando complexos de cinema para empresa: {}", principal.getCompanyId());

        return cinemaComplexRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca um complexo de cinema por ID.
     */
    @Transactional(readOnly = true)
    public CinemaComplexResponse getCinemaComplexById(Long id, UserPrincipal principal) {
        log.info("Buscando complexo de cinema ID: {} para empresa: {}", id, principal.getCompanyId());

        CinemaComplex cinemaComplex = cinemaComplexRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Complexo de cinema não encontrado"));

        // Validar se pertence à empresa do usuário
        if (!cinemaComplex.getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("Acesso negado ao complexo de cinema");
        }

        return toResponse(cinemaComplex);
    }

    /**
     * Atualiza um complexo de cinema.
     */
    @Transactional
    public CinemaComplexResponse updateCinemaComplex(Long id, UpdateCinemaComplexRequest request,
                                                     UserPrincipal principal) {
        log.info("Atualizando complexo de cinema ID: {} para empresa: {}", id, principal.getCompanyId());

        CinemaComplex cinemaComplex = cinemaComplexRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Complexo de cinema não encontrado"));

        // Validar se pertence à empresa do usuário
        if (!cinemaComplex.getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("Acesso negado ao complexo de cinema");
        }

        // Validar CNPJ se estiver sendo alterado
        if (request.cnpj() != null && !request.cnpj().equals(cinemaComplex.getCnpj())) {
            String sanitizedCnpj = sanitizeNumericField(request.cnpj());
            if (cinemaComplexRepository.existsByCnpj(sanitizedCnpj)) {
                throw new BusinessException("Já existe um complexo de cinema com o CNPJ: " + request.cnpj());
            }
            cinemaComplex.setCnpj(sanitizedCnpj);
        }

        // Atualizar apenas os campos que foram fornecidos
        if (request.name() != null) {
            cinemaComplex.setName(request.name());
        }
        if (request.address() != null) {
            cinemaComplex.setAddress(request.address());
        }
        if (request.city() != null) {
            cinemaComplex.setCity(request.city());
        }
        if (request.state() != null) {
            cinemaComplex.setState(request.state());
        }
        if (request.postalCode() != null) {
            cinemaComplex.setPostalCode(sanitizeNumericField(request.postalCode()));
        }
        if (request.ibgeMunicipalityCode() != null) {
            cinemaComplex.setIbgeMunicipalityCode(request.ibgeMunicipalityCode());
        }
        if (request.ancineRegistry() != null) {
            cinemaComplex.setAncineRegistry(request.ancineRegistry());
        }
        if (request.openingDate() != null) {
            cinemaComplex.setOpeningDate(request.openingDate());
        }
        if (request.active() != null) {
            cinemaComplex.setActive(request.active());
        }

        cinemaComplex.setUpdatedAt(Instant.now());

        CinemaComplex updated = cinemaComplexRepository.save(cinemaComplex);
        log.info("Complexo de cinema atualizado com sucesso: ID {}", updated.getId());

        return toResponse(updated);
    }

    /**
     * Deleta um complexo de cinema.
     */
    @Transactional
    public void deleteCinemaComplex(Long id, UserPrincipal principal) {
        log.info("Deletando complexo de cinema ID: {} para empresa: {}", id, principal.getCompanyId());

        CinemaComplex cinemaComplex = cinemaComplexRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Complexo de cinema não encontrado"));

        // Validar se pertence à empresa do usuário
        if (!cinemaComplex.getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("Acesso negado ao complexo de cinema");
        }

        // Validar se possui salas associadas
        if (!cinemaComplex.getRooms().isEmpty()) {
            throw new BusinessException("Não é possível deletar um complexo de cinema com salas cadastradas");
        }

        cinemaComplexRepository.delete(cinemaComplex);
        log.info("Complexo de cinema deletado com sucesso: ID {}", id);
    }

    /**
     * Converte entidade para DTO de response.
     */
    private CinemaComplexResponse toResponse(CinemaComplex cinemaComplex) {
        return new CinemaComplexResponse(
                cinemaComplex.getId(),
                cinemaComplex.getCompanyId(),
                cinemaComplex.getName(),
                cinemaComplex.getCode(),
                cinemaComplex.getCnpj(),
                cinemaComplex.getAddress(),
                cinemaComplex.getCity(),
                cinemaComplex.getState(),
                cinemaComplex.getPostalCode(),
                cinemaComplex.getIbgeMunicipalityCode(),
                cinemaComplex.getAncineRegistry(),
                cinemaComplex.getOpeningDate(),
                cinemaComplex.getActive(),
                cinemaComplex.getCreatedAt(),
                cinemaComplex.getUpdatedAt());
    }
}
