package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSessionLanguageRequest;
import com.frame24.api.operations.application.dto.SessionLanguageResponse;
import com.frame24.api.operations.application.dto.UpdateSessionLanguageRequest;
import com.frame24.api.operations.domain.SessionLanguage;
import com.frame24.api.operations.infrastructure.repository.SessionLanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionLanguageService {

    private final SessionLanguageRepository sessionLanguageRepository;

    @Transactional
    public SessionLanguageResponse create(CreateSessionLanguageRequest request, UserPrincipal principal) {
        log.info("Criando idioma de sessão: name={}, abbr={}, companyId={}", request.name(), request.abbreviation(),
                principal.getCompanyId());

        if (sessionLanguageRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um idioma com este nome");
        }
        if (request.abbreviation() != null && sessionLanguageRepository
                .existsByCompanyIdAndAbbreviation(principal.getCompanyId(), request.abbreviation())) {
            throw new ValidationException("Já existe um idioma com esta abreviação");
        }

        SessionLanguage language = new SessionLanguage();
        language.setCompanyId(principal.getCompanyId());
        language.setName(request.name());
        language.setDescription(request.description());
        language.setAbbreviation(request.abbreviation());
        language.setCreatedAt(Instant.now());

        sessionLanguageRepository.save(language);
        return toResponse(language);
    }

    @Transactional(readOnly = true)
    public List<SessionLanguageResponse> list(UserPrincipal principal) {
        return sessionLanguageRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionLanguageResponse getById(Long id, UserPrincipal principal) {
        SessionLanguage language = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(language);
    }

    @Transactional
    public SessionLanguageResponse update(Long id, UpdateSessionLanguageRequest request, UserPrincipal principal) {
        SessionLanguage language = findByIdAndCompany(id, principal.getCompanyId());

        if (request.name() != null && !request.name().equals(language.getName())) {
            if (sessionLanguageRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um idioma com este nome");
            }
            language.setName(request.name());
        }

        if (request.abbreviation() != null && !request.abbreviation().equals(language.getAbbreviation())) {
            if (sessionLanguageRepository.existsByCompanyIdAndAbbreviation(principal.getCompanyId(),
                    request.abbreviation())) {
                throw new ValidationException("Já existe um idioma com esta abreviação");
            }
            language.setAbbreviation(request.abbreviation());
        }

        if (request.description() != null)
            language.setDescription(request.description());

        sessionLanguageRepository.save(language);
        return toResponse(language);
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        SessionLanguage language = findByIdAndCompany(id, principal.getCompanyId());
        // TODO: Check usage ...
        sessionLanguageRepository.delete(language);
    }

    private SessionLanguage findByIdAndCompany(Long id, Long companyId) {
        return sessionLanguageRepository.findById(id)
                .filter(l -> l.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Idioma de sessão não encontrado"));
    }

    private SessionLanguageResponse toResponse(SessionLanguage language) {
        return new SessionLanguageResponse(
                language.getId(),
                language.getName(),
                language.getDescription(),
                language.getAbbreviation(),
                language.getCreatedAt());
    }
}
