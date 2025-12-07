package com.frame24.api.operations.application.service;

import com.frame24.api.common.event.CompanyCreatedEvent;
import com.frame24.api.operations.domain.*;
import com.frame24.api.operations.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service para criar tipos padrão de projeção, áudio, assentos, status de
 * assentos
 * e status/idiomas de sessão ao registrar uma nova empresa.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultOperationTypesService {

    private final ProjectionTypeRepository projectionTypeRepository;
    private final AudioTypeRepository audioTypeRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final SessionStatusRepository sessionStatusRepository;
    private final SessionLanguageRepository sessionLanguageRepository;

    /**
     * Tipos de projeção padrão para novas empresas.
     */
    private static final List<TypeConfig> DEFAULT_PROJECTION_TYPES = List.of(
            new TypeConfig("2D Standard", "Projeção digital 2D padrão", BigDecimal.ZERO),
            new TypeConfig("3D", "Projeção digital 3D", new BigDecimal("8.00")),
            new TypeConfig("IMAX", "Projeção IMAX em tela gigante", new BigDecimal("15.00")),
            new TypeConfig("IMAX 3D", "Projeção IMAX 3D", new BigDecimal("20.00")),
            new TypeConfig("4DX", "Experiência 4D com movimentos e efeitos", new BigDecimal("25.00")),
            new TypeConfig("D-Box", "Assentos com movimento sincronizado", new BigDecimal("12.00")));

    /**
     * Tipos de áudio padrão para novas empresas.
     */
    private static final List<TypeConfig> DEFAULT_AUDIO_TYPES = List.of(
            new TypeConfig("Dolby Digital 5.1", "Sistema padrão 5.1 canais", BigDecimal.ZERO),
            new TypeConfig("Dolby Digital 7.1", "Sistema 7.1 canais surround", new BigDecimal("3.00")),
            new TypeConfig("Dolby Atmos", "Sistema imersivo Dolby Atmos", new BigDecimal("8.00")),
            new TypeConfig("DTS:X", "Sistema surround DTS:X", new BigDecimal("5.00")),
            new TypeConfig("IMAX Sound", "Sistema de som IMAX exclusivo", new BigDecimal("10.00")));

    /**
     * Tipos de assento padrão.
     */
    private static final List<TypeConfig> DEFAULT_SEAT_TYPES = List.of(
            new TypeConfig("Standard", "Assento padrão", BigDecimal.ZERO),
            new TypeConfig("VIP", "Assento VIP reclinável", new BigDecimal("15.00")),
            new TypeConfig("Premium", "Assento Premium com mesa", new BigDecimal("25.00")),
            new TypeConfig("Wheelchair", "Espaço para cadeirante", BigDecimal.ZERO),
            new TypeConfig("Companion", "Assento para acompanhante", BigDecimal.ZERO));

    /**
     * Status de assento padrão.
     */
    private static final List<StatusConfig> DEFAULT_SEAT_STATUSES = List.of(
            new StatusConfig("Available", "Assento disponível para venda", true, true),
            new StatusConfig("Blocked", "Assento bloqueado", false, false),
            new StatusConfig("Maintenance", "Assento em manutenção", false, false),
            new StatusConfig("Reserved", "Assento reservado", false, false),
            new StatusConfig("House/Management", "Reservado para gestão", false, false));

    /**
     * Status de sessão padrão.
     */
    private static final List<SessionStatusConfig> DEFAULT_SESSION_STATUSES = List.of(
            new SessionStatusConfig("Scheduled", "Sessão agendada (não visível)", true),
            new SessionStatusConfig("Selling", "Sessão com vendas abertas", true),
            new SessionStatusConfig("Closed", "Vendas encerradas", true),
            new SessionStatusConfig("Cancelled", "Sessão cancelada", false),
            new SessionStatusConfig("Finished", "Sessão finalizada", false));

    /**
     * Idiomas de sessão padrão.
     */
    private static final List<LanguageConfig> DEFAULT_SESSION_LANGUAGES = List.of(
            new LanguageConfig("Original", "Áudio original com legendas", "LEG"),
            new LanguageConfig("Dubbed", "Áudio dublado em português", "DUB"),
            new LanguageConfig("National", "Filme nacional", "NAC"),
            new LanguageConfig("Dual Audio", "Opção de áudio original ou dublado", "DUAL"));

    @EventListener
    @Transactional
    public void handleCompanyCreated(CompanyCreatedEvent event) {
        log.info("Evento de empresa criada recebido: {} (id={})", event.corporateName(), event.companyId());
        createDefaultTypes(event.companyId());
    }

    public void createDefaultTypes(Long companyId) {
        log.info("Criando tipos padrão de operações para empresa: {}", companyId);

        // Projeção
        for (TypeConfig config : DEFAULT_PROJECTION_TYPES) {
            createProjectionType(companyId, config);
        }

        // Áudio
        for (TypeConfig config : DEFAULT_AUDIO_TYPES) {
            createAudioType(companyId, config);
        }

        // Assentos
        for (TypeConfig config : DEFAULT_SEAT_TYPES) {
            createSeatType(companyId, config);
        }

        // Status de Assentos
        for (StatusConfig config : DEFAULT_SEAT_STATUSES) {
            createSeatStatus(companyId, config);
        }

        // Status de Sessão
        for (SessionStatusConfig config : DEFAULT_SESSION_STATUSES) {
            createSessionStatus(companyId, config);
        }

        // Idiomas de Sessão
        for (LanguageConfig config : DEFAULT_SESSION_LANGUAGES) {
            createSessionLanguage(companyId, config);
        }

        log.info("Tipos padrão criados com sucesso para empresa: {}", companyId);
    }

    private void createProjectionType(Long companyId, TypeConfig config) {
        ProjectionType type = new ProjectionType();
        type.setCompanyId(companyId);
        type.setName(config.name());
        type.setDescription(config.description());
        type.setAdditionalValue(config.additionalValue());
        type.setCreatedAt(Instant.now());
        projectionTypeRepository.save(type);
    }

    private void createAudioType(Long companyId, TypeConfig config) {
        AudioType type = new AudioType();
        type.setCompanyId(companyId);
        type.setName(config.name());
        type.setDescription(config.description());
        type.setAdditionalValue(config.additionalValue());
        type.setCreatedAt(Instant.now());
        audioTypeRepository.save(type);
    }

    private void createSeatType(Long companyId, TypeConfig config) {
        SeatType type = new SeatType();
        type.setCompanyId(companyId);
        type.setName(config.name());
        type.setDescription(config.description());
        type.setAdditionalValue(config.additionalValue());
        type.setCreatedAt(Instant.now());
        seatTypeRepository.save(type);
    }

    private void createSeatStatus(Long companyId, StatusConfig config) {
        SeatStatus status = new SeatStatus();
        status.setCompanyId(companyId);
        status.setName(config.name());
        status.setDescription(config.description());
        status.setAllowsModification(config.allowsModification());
        status.setIsDefault(config.isDefault());
        status.setCreatedAt(Instant.now());
        seatStatusRepository.save(status);
    }

    private void createSessionStatus(Long companyId, SessionStatusConfig config) {
        SessionStatus status = new SessionStatus();
        status.setCompanyId(companyId);
        status.setName(config.name());
        status.setDescription(config.description());
        status.setAllowsModification(config.allowsModification());
        status.setCreatedAt(Instant.now());
        sessionStatusRepository.save(status);
    }

    private void createSessionLanguage(Long companyId, LanguageConfig config) {
        SessionLanguage language = new SessionLanguage();
        language.setCompanyId(companyId);
        language.setName(config.name());
        language.setDescription(config.description());
        language.setAbbreviation(config.abbreviation());
        language.setCreatedAt(Instant.now());
        sessionLanguageRepository.save(language);
    }

    private record TypeConfig(String name, String description, BigDecimal additionalValue) {
    }

    private record StatusConfig(String name, String description, boolean allowsModification, boolean isDefault) {
    }

    private record SessionStatusConfig(String name, String description, boolean allowsModification) {
    }

    private record LanguageConfig(String name, String description, String abbreviation) {
    }
}
