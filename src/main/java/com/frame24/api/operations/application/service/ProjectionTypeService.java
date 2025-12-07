package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateProjectionTypeRequest;
import com.frame24.api.operations.application.dto.ProjectionTypeResponse;
import com.frame24.api.operations.application.dto.UpdateProjectionTypeRequest;
import com.frame24.api.operations.domain.ProjectionType;
import com.frame24.api.operations.infrastructure.repository.ProjectionTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service para gerenciamento de tipos de projeção.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectionTypeService {

    private final ProjectionTypeRepository projectionTypeRepository;

    /**
     * Cria um novo tipo de projeção.
     */
    @Transactional
    public ProjectionTypeResponse create(CreateProjectionTypeRequest request, UserPrincipal principal) {
        log.info("Criando tipo de projeção: name={}, companyId={}", request.name(), principal.getCompanyId());

        // Validar nome único
        if (projectionTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um tipo de projeção com este nome");
        }

        ProjectionType projectionType = new ProjectionType();
        projectionType.setCompanyId(principal.getCompanyId());
        projectionType.setName(request.name());
        projectionType.setDescription(request.description());
        projectionType
                .setAdditionalValue(request.additionalValue() != null ? request.additionalValue() : BigDecimal.ZERO);
        projectionType.setCreatedAt(Instant.now());

        projectionTypeRepository.save(projectionType);
        log.info("Tipo de projeção criado: id={}", projectionType.getId());

        return toResponse(projectionType);
    }

    /**
     * Lista todos os tipos de projeção da empresa.
     */
    @Transactional(readOnly = true)
    public List<ProjectionTypeResponse> list(UserPrincipal principal) {
        log.debug("Listando tipos de projeção: companyId={}", principal.getCompanyId());

        return projectionTypeRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca tipo de projeção por ID.
     */
    @Transactional(readOnly = true)
    public ProjectionTypeResponse getById(Long id, UserPrincipal principal) {
        log.debug("Buscando tipo de projeção: id={}", id);

        ProjectionType projectionType = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(projectionType);
    }

    /**
     * Atualiza um tipo de projeção.
     */
    @Transactional
    public ProjectionTypeResponse update(Long id, UpdateProjectionTypeRequest request, UserPrincipal principal) {
        log.info("Atualizando tipo de projeção: id={}", id);

        ProjectionType projectionType = findByIdAndCompany(id, principal.getCompanyId());

        // Validar nome único se estiver sendo alterado
        if (request.name() != null && !request.name().equals(projectionType.getName())) {
            if (projectionTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um tipo de projeção com este nome");
            }
            projectionType.setName(request.name());
        }

        if (request.description() != null) {
            projectionType.setDescription(request.description());
        }

        if (request.additionalValue() != null) {
            projectionType.setAdditionalValue(request.additionalValue());
        }

        projectionTypeRepository.save(projectionType);
        log.info("Tipo de projeção atualizado: id={}", id);

        return toResponse(projectionType);
    }

    /**
     * Deleta um tipo de projeção.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        log.info("Deletando tipo de projeção: id={}", id);

        ProjectionType projectionType = findByIdAndCompany(id, principal.getCompanyId());

        // Verificar se não está em uso
        if (!projectionType.getRooms().isEmpty()) {
            throw new ValidationException("Não é possível deletar: tipo de projeção está em uso por " +
                    projectionType.getRooms().size() + " sala(s)");
        }

        projectionTypeRepository.delete(projectionType);
        log.info("Tipo de projeção deletado: id={}", id);
    }

    private ProjectionType findByIdAndCompany(Long id, Long companyId) {
        return projectionTypeRepository.findById(id)
                .filter(pt -> pt.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de projeção não encontrado"));
    }

    private ProjectionTypeResponse toResponse(ProjectionType projectionType) {
        return new ProjectionTypeResponse(
                projectionType.getId(),
                projectionType.getName(),
                projectionType.getDescription(),
                projectionType.getAdditionalValue(),
                projectionType.getCreatedAt());
    }
}
