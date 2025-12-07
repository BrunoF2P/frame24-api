package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSeatStatusRequest;
import com.frame24.api.operations.application.dto.SeatStatusResponse;
import com.frame24.api.operations.application.dto.UpdateSeatStatusRequest;
import com.frame24.api.operations.domain.SeatStatus;
import com.frame24.api.operations.infrastructure.repository.SeatStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de status de assento.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatStatusService {

    private final SeatStatusRepository seatStatusRepository;

    /**
     * Cria um novo status de assento.
     */
    @Transactional
    public SeatStatusResponse create(CreateSeatStatusRequest request, UserPrincipal principal) {
        log.info("Criando status de assento: name={}, companyId={}", request.name(), principal.getCompanyId());

        // Validar nome único
        if (seatStatusRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um status de assento com este nome");
        }

        // Se for definir como padrão, remover padrão anterior
        if (Boolean.TRUE.equals(request.isDefault())) {
            unsetCurrentDefault(principal.getCompanyId());
        }

        SeatStatus seatStatus = new SeatStatus();
        seatStatus.setCompanyId(principal.getCompanyId());
        seatStatus.setName(request.name());
        seatStatus.setDescription(request.description());
        seatStatus.setAllowsModification(request.allowsModification());
        seatStatus.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
        seatStatus.setCreatedAt(Instant.now());

        seatStatusRepository.save(seatStatus);
        log.info("Status de assento criado: id={}", seatStatus.getId());

        return toResponse(seatStatus);
    }

    /**
     * Lista todos os status de assento da empresa.
     */
    @Transactional(readOnly = true)
    public List<SeatStatusResponse> list(UserPrincipal principal) {
        log.debug("Listando status de assento: companyId={}", principal.getCompanyId());

        return seatStatusRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca status de assento por ID.
     */
    @Transactional(readOnly = true)
    public SeatStatusResponse getById(Long id, UserPrincipal principal) {
        log.debug("Buscando status de assento: id={}", id);

        SeatStatus seatStatus = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(seatStatus);
    }

    /**
     * Atualiza um status de assento.
     */
    @Transactional
    public SeatStatusResponse update(Long id, UpdateSeatStatusRequest request, UserPrincipal principal) {
        log.info("Atualizando status de assento: id={}", id);

        SeatStatus seatStatus = findByIdAndCompany(id, principal.getCompanyId());

        // Validar nome único se estiver sendo alterado
        if (request.name() != null && !request.name().equals(seatStatus.getName())) {
            if (seatStatusRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um status de assento com este nome");
            }
            seatStatus.setName(request.name());
        }

        if (request.description() != null) {
            seatStatus.setDescription(request.description());
        }

        if (request.allowsModification() != null) {
            seatStatus.setAllowsModification(request.allowsModification());
        }

        // Lógica para isDefault
        if (request.isDefault() != null) {
            if (request.isDefault()) {
                // Se está marcando como Default, remove o anterior (ID diferente)
                unsetCurrentDefault(principal.getCompanyId(), id);
                seatStatus.setIsDefault(true);
            } else {
                // Se está desmarcando, verifica se há outro padrão
                if (seatStatus.getIsDefault()) {
                    // Não pode remover o status de Default sem definir outro, idealmente.
                    // Mas vamos permitir, assumindo que sistema lidará com 'sem default' ou usuario
                    // definirá outro.
                    // Opcionalmente, poderiamos impedir. Por enquanto, permitimos.
                    seatStatus.setIsDefault(false);
                }
            }
        }

        seatStatusRepository.save(seatStatus);
        log.info("Status de assento atualizado: id={}", id);

        return toResponse(seatStatus);
    }

    /**
     * Deleta um status de assento.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        log.info("Deletando status de assento: id={}", id);

        SeatStatus seatStatus = findByIdAndCompany(id, principal.getCompanyId());

        if (seatStatus.getIsDefault()) {
            throw new ValidationException("Não é possível deletar o status padrão. Defina outro como padrão antes.");
        }

        // TODO: Verificar se está em uso por assentos quando implementarmos Seat
        // if (!seatStatus.getSeats().isEmpty()) { exception... }

        seatStatusRepository.delete(seatStatus);
        log.info("Status de assento deletado: id={}", id);
    }

    private void unsetCurrentDefault(Long companyId) {
        unsetCurrentDefault(companyId, null);
    }

    private void unsetCurrentDefault(Long companyId, Long ignoreId) {
        Optional<SeatStatus> currentDefault = seatStatusRepository.findByCompanyIdAndIsDefaultTrue(companyId);
        currentDefault.ifPresent(status -> {
            if (ignoreId == null || !status.getId().equals(ignoreId)) {
                status.setIsDefault(false);
                seatStatusRepository.save(status);
                log.info("Status {} deixou de ser padrão", status.getName());
            }
        });
    }

    private SeatStatus findByIdAndCompany(Long id, Long companyId) {
        return seatStatusRepository.findById(id)
                .filter(ss -> ss.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Status de assento não encontrado"));
    }

    private SeatStatusResponse toResponse(SeatStatus seatStatus) {
        return new SeatStatusResponse(
                seatStatus.getId(),
                seatStatus.getName(),
                seatStatus.getDescription(),
                seatStatus.getAllowsModification(),
                seatStatus.getIsDefault(),
                seatStatus.getCreatedAt());
    }
}
