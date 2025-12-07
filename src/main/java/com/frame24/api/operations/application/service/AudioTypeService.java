package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.AudioTypeResponse;
import com.frame24.api.operations.application.dto.CreateAudioTypeRequest;
import com.frame24.api.operations.application.dto.UpdateAudioTypeRequest;
import com.frame24.api.operations.domain.AudioType;
import com.frame24.api.operations.infrastructure.repository.AudioTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service para gerenciamento de tipos de áudio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudioTypeService {

    private final AudioTypeRepository audioTypeRepository;

    /**
     * Cria um novo tipo de áudio.
     */
    @Transactional
    public AudioTypeResponse create(CreateAudioTypeRequest request, UserPrincipal principal) {
        log.info("Criando tipo de áudio: name={}, companyId={}", request.name(), principal.getCompanyId());

        // Validar nome único
        if (audioTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
            throw new ValidationException("Já existe um tipo de áudio com este nome");
        }

        AudioType audioType = new AudioType();
        audioType.setCompanyId(principal.getCompanyId());
        audioType.setName(request.name());
        audioType.setDescription(request.description());
        audioType.setAdditionalValue(request.additionalValue() != null ? request.additionalValue() : BigDecimal.ZERO);
        audioType.setCreatedAt(Instant.now());

        audioTypeRepository.save(audioType);
        log.info("Tipo de áudio criado: id={}", audioType.getId());

        return toResponse(audioType);
    }

    /**
     * Lista todos os tipos de áudio da empresa.
     */
    @Transactional(readOnly = true)
    public List<AudioTypeResponse> list(UserPrincipal principal) {
        log.debug("Listando tipos de áudio: companyId={}", principal.getCompanyId());

        return audioTypeRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca tipo de áudio por ID.
     */
    @Transactional(readOnly = true)
    public AudioTypeResponse getById(Long id, UserPrincipal principal) {
        log.debug("Buscando tipo de áudio: id={}", id);

        AudioType audioType = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(audioType);
    }

    /**
     * Atualiza um tipo de áudio.
     */
    @Transactional
    public AudioTypeResponse update(Long id, UpdateAudioTypeRequest request, UserPrincipal principal) {
        log.info("Atualizando tipo de áudio: id={}", id);

        AudioType audioType = findByIdAndCompany(id, principal.getCompanyId());

        // Validar nome único se estiver sendo alterado
        if (request.name() != null && !request.name().equals(audioType.getName())) {
            if (audioTypeRepository.existsByCompanyIdAndName(principal.getCompanyId(), request.name())) {
                throw new ValidationException("Já existe um tipo de áudio com este nome");
            }
            audioType.setName(request.name());
        }

        if (request.description() != null) {
            audioType.setDescription(request.description());
        }

        if (request.additionalValue() != null) {
            audioType.setAdditionalValue(request.additionalValue());
        }

        audioTypeRepository.save(audioType);
        log.info("Tipo de áudio atualizado: id={}", id);

        return toResponse(audioType);
    }

    /**
     * Deleta um tipo de áudio.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        log.info("Deletando tipo de áudio: id={}", id);

        AudioType audioType = findByIdAndCompany(id, principal.getCompanyId());

        // Verificar se não está em uso
        if (!audioType.getRooms().isEmpty()) {
            throw new ValidationException("Não é possível deletar: tipo de áudio está em uso por " +
                    audioType.getRooms().size() + " sala(s)");
        }

        audioTypeRepository.delete(audioType);
        log.info("Tipo de áudio deletado: id={}", id);
    }

    private AudioType findByIdAndCompany(Long id, Long companyId) {
        return audioTypeRepository.findById(id)
                .filter(at -> at.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de áudio não encontrado"));
    }

    private AudioTypeResponse toResponse(AudioType audioType) {
        return new AudioTypeResponse(
                audioType.getId(),
                audioType.getName(),
                audioType.getDescription(),
                audioType.getAdditionalValue(),
                audioType.getCreatedAt());
    }
}
