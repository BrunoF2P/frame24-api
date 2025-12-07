package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSessionStatusRequest;
import com.frame24.api.operations.application.dto.SessionStatusResponse;
import com.frame24.api.operations.application.dto.UpdateSessionStatusRequest;
import com.frame24.api.operations.domain.SessionStatus;
import com.frame24.api.operations.infrastructure.repository.SessionStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionStatusService {

    private final SessionStatusRepository sessionStatusRepository;

    @Transactional
    public SessionStatusResponse create(CreateSessionStatusRequest request, UserPrincipal principal) {
        log.info("Criando status de sessão: name={}, companyId={}", request.name(), principal.getCompanyId());

        if (sessionStatusRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um status de sessão com este nome");
        }

        SessionStatus status = new SessionStatus();
        status.setCompanyId(principal.getCompanyId());
        status.setName(request.name());
        status.setDescription(request.description());
        status.setAllowsModification(request.allowsModification());
        status.setCreatedAt(Instant.now());

        sessionStatusRepository.save(status);
        return toResponse(status);
    }

    @Transactional(readOnly = true)
    public List<SessionStatusResponse> list(UserPrincipal principal) {
        return sessionStatusRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionStatusResponse getById(Long id, UserPrincipal principal) {
        SessionStatus status = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(status);
    }

    @Transactional
    public SessionStatusResponse update(Long id, UpdateSessionStatusRequest request, UserPrincipal principal) {
        SessionStatus status = findByIdAndCompany(id, principal.getCompanyId());

        if (request.name() != null && !request.name().equals(status.getName())) {
            if (sessionStatusRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um status de sessão com este nome");
            }
            status.setName(request.name());
        }

        if (request.description() != null)
            status.setDescription(request.description());
        if (request.allowsModification() != null)
            status.setAllowsModification(request.allowsModification());

        sessionStatusRepository.save(status);
        return toResponse(status);
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        SessionStatus status = findByIdAndCompany(id, principal.getCompanyId());

        // TODO: Check usage in ShowtimeSchedule
        // if (!status.getShowtimeSchedules().isEmpty()) ...

        sessionStatusRepository.delete(status);
    }

    private SessionStatus findByIdAndCompany(Long id, Long companyId) {
        return sessionStatusRepository.findById(id)
                .filter(s -> s.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Status de sessão não encontrado"));
    }

    private SessionStatusResponse toResponse(SessionStatus status) {
        return new SessionStatusResponse(
                status.getId(),
                status.getName(),
                status.getDescription(),
                status.getAllowsModification(),
                status.getCreatedAt());
    }
}
