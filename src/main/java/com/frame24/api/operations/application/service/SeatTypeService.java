package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSeatTypeRequest;
import com.frame24.api.operations.application.dto.SeatTypeResponse;
import com.frame24.api.operations.application.dto.UpdateSeatTypeRequest;
import com.frame24.api.operations.domain.SeatType;
import com.frame24.api.operations.infrastructure.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service para gerenciamento de tipos de assento.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatTypeService {

    private final SeatTypeRepository seatTypeRepository;

    /**
     * Cria um novo tipo de assento.
     */
    @Transactional
    public SeatTypeResponse create(CreateSeatTypeRequest request, UserPrincipal principal) {
        log.info("Criando tipo de assento: name={}, companyId={}", request.name(), principal.getCompanyId());

        // Validar nome único
        if (seatTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um tipo de assento com este nome");
        }

        SeatType seatType = new SeatType();
        seatType.setCompanyId(principal.getCompanyId());
        seatType.setName(request.name());
        seatType.setDescription(request.description());
        seatType.setAdditionalValue(request.additionalValue() != null ? request.additionalValue() : BigDecimal.ZERO);
        seatType.setCreatedAt(Instant.now());

        seatTypeRepository.save(seatType);
        log.info("Tipo de assento criado: id={}", seatType.getId());

        return toResponse(seatType);
    }

    /**
     * Lista todos os tipos de assento da empresa.
     */
    @Transactional(readOnly = true)
    public List<SeatTypeResponse> list(UserPrincipal principal) {
        log.debug("Listando tipos de assento: companyId={}", principal.getCompanyId());

        return seatTypeRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca tipo de assento por ID.
     */
    @Transactional(readOnly = true)
    public SeatTypeResponse getById(Long id, UserPrincipal principal) {
        log.debug("Buscando tipo de assento: id={}", id);

        SeatType seatType = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(seatType);
    }

    /**
     * Atualiza um tipo de assento.
     */
    @Transactional
    public SeatTypeResponse update(Long id, UpdateSeatTypeRequest request, UserPrincipal principal) {
        log.info("Atualizando tipo de assento: id={}", id);

        SeatType seatType = findByIdAndCompany(id, principal.getCompanyId());

        // Validar nome único se estiver sendo alterado
        if (request.name() != null && !request.name().equals(seatType.getName())) {
            if (seatTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um tipo de assento com este nome");
            }
            seatType.setName(request.name());
        }

        if (request.description() != null) {
            seatType.setDescription(request.description());
        }

        if (request.additionalValue() != null) {
            seatType.setAdditionalValue(request.additionalValue());
        }

        seatTypeRepository.save(seatType);
        log.info("Tipo de assento atualizado: id={}", id);

        return toResponse(seatType);
    }

    /**
     * Deleta um tipo de assento.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        log.info("Deletando tipo de assento: id={}", id);

        SeatType seatType = findByIdAndCompany(id, principal.getCompanyId());

        // Verificar se não está em uso
        if (!seatType.getSeats().isEmpty()) {
            throw new ValidationException("Não é possível deletar: tipo de assento está em uso por " +
                    seatType.getSeats().size() + " assento(s)");
        }

        seatTypeRepository.delete(seatType);
        log.info("Tipo de assento deletado: id={}", id);
    }

    private SeatType findByIdAndCompany(Long id, Long companyId) {
        return seatTypeRepository.findById(id)
                .filter(st -> st.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de assento não encontrado"));
    }

    private SeatTypeResponse toResponse(SeatType seatType) {
        return new SeatTypeResponse(
                seatType.getId(),
                seatType.getName(),
                seatType.getDescription(),
                seatType.getAdditionalValue(),
                seatType.getCreatedAt());
    }
}
