package com.frame24.api.identity.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.identity.application.dto.*;
import com.frame24.api.identity.domain.*;
import com.frame24.api.identity.domain.enums.IdentityType;
import com.frame24.api.identity.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de usuários seguindo padrões enterprise.
 * Implementa CRUD completo com validação de permissões, RLS e auditoria.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final CompanyUserRepository companyUserRepository;
    private final IdentityRepository identityRepository;
    private final PersonRepository personRepository;
    private final CustomRoleRepository customRoleRepository;
    private final CompanyRepository companyRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final JsonMapper jsonMapper;

    /**
     * Cria um novo usuário no sistema.
     *
     * @param request   Dados do usuário
     * @param principal Usuário autenticado
     * @return Usuário criado
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request, UserPrincipal principal) {
        log.info("Criando novo usuário: email={}, companyId={}", request.email(), principal.getCompanyId());

        // Validações
        validateEmailUniqueness(request.email());
        if (request.cpf() != null) {
            validateCpfUniqueness(request.cpf());
        }
        validatePasswordRequirements(request.password());
        validateRole(request.roleId(), principal);
        validateAllowedComplexes(request.allowedComplexes(), principal.getCompanyId(), principal);

        // Gerar employee_id
        Long employeeId = generateNextEmployeeId(principal.getCompanyId());

        // Criar Person
        Person person = createPerson(request);
        personRepository.save(person);
        log.debug("Person criada: id={}", person.getId());

        // Criar Identity
        Identity identity = createIdentity(request, person);
        identityRepository.save(identity);
        log.debug("Identity criada: id={}", identity.getId());

        // Criar CompanyUser
        CompanyUser companyUser = createCompanyUser(request, identity, employeeId, principal);
        companyUserRepository.save(companyUser);
        log.info("Usuário criado com sucesso: id={}, employeeId={}", companyUser.getId(), employeeId);

        return toUserResponse(companyUser);
    }

    /**
     * Lista todos os usuários da empresa com paginação.
     *
     * @param pageable  Parâmetros de paginação
     * @param principal Usuário autenticado
     * @return Lista paginada de usuários
     */
    @Transactional(readOnly = true)
    public UserListResponse listUsers(Pageable pageable, UserPrincipal principal) {
        log.debug("Listando usuários: companyId={}, page={}", principal.getCompanyId(), pageable.getPageNumber());

        Page<CompanyUser> page = companyUserRepository.findByCompanyId(principal.getCompanyId(), pageable);

        List<UserResponse> users = page.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return new UserListResponse(
                users,
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getTotalPages());
    }

    /**
     * Busca usuários com filtros.
     *
     * @param searchRequest Critérios de busca
     * @param principal     Usuário autenticado
     * @return Lista paginada filtrada
     */
    @Transactional(readOnly = true)
    public UserListResponse searchUsers(SearchUserRequest searchRequest, UserPrincipal principal) {
        log.debug("Buscando usuários com filtros: employeeId={}, email={}, name={}",
                searchRequest.employeeId(), searchRequest.email(), searchRequest.fullName());

        // Configurar ordenação
        Sort sort = Sort.by(
                searchRequest.getSortDirectionOrDefault().equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                searchRequest.getSortByOrDefault());

        Pageable pageable = PageRequest.of(
                searchRequest.getPageOrDefault(),
                searchRequest.getSizeOrDefault(),
                sort);

        // Executar busca com filtros
        Page<CompanyUser> page = companyUserRepository.searchUsers(
                principal.getCompanyId(),
                searchRequest.employeeId(),
                searchRequest.email(),
                searchRequest.fullName(),
                searchRequest.department(),
                searchRequest.active(),
                searchRequest.roleId(),
                pageable);

        List<UserResponse> users = page.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return new UserListResponse(
                users,
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getTotalPages());
    }

    /**
     * Busca usuário por ID.
     *
     * @param id        ID do usuário
     * @param principal Usuário autenticado
     * @return Usuário encontrado
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, UserPrincipal principal) {
        log.debug("Buscando usuário: id={}", id);

        CompanyUser user = companyUserRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // RLS já valida o acesso, mas fazemos double-check
        if (!user.getCompany().getId().equals(principal.getCompanyId())) {
            throw new NotFoundException("Usuário não encontrado");
        }

        return toUserResponse(user);
    }

    /**
     * Busca usuário por employee ID.
     *
     * @param employeeId ID do funcionário
     * @param principal  Usuário autenticado
     * @return Usuário encontrado
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmployeeId(Long employeeId, UserPrincipal principal) {
        log.debug("Buscando usuário por employeeId: {}", employeeId);

        CompanyUser user = companyUserRepository.findByEmployeeIdAndCompanyId(employeeId, principal.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        return toUserResponse(user);
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id        ID do usuário
     * @param request   Dados para atualização
     * @param principal Usuário autenticado
     * @return Usuário atualizado
     */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal principal) {
        log.info("Atualizando usuário: id={}", id);

        CompanyUser user = companyUserRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // Validar hierarquia de permissões
        validateRoleHierarchy(user.getRole().getId(), principal);

        // Atualizar Person
        if (hasPersonUpdates(request)) {
            updatePerson(user.getIdentity().getPerson(), request);
        }

        // Atualizar Identity
        if (hasIdentityUpdates(request)) {
            updateIdentity(user.getIdentity(), request);
        }

        // Atualizar CompanyUser
        updateCompanyUser(user, request, principal);

        companyUserRepository.save(user);
        log.info("Usuário atualizado com sucesso: id={}", id);

        return toUserResponse(user);
    }

    /**
     * Desativa um usuário (soft delete).
     *
     * @param id        ID do usuário
     * @param principal Usuário autenticado
     */
    @Transactional
    public void deactivateUser(Long id, UserPrincipal principal) {
        log.info("Desativando usuário: id={}", id);

        CompanyUser user = companyUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // Validar hierarquia
        validateRoleHierarchy(user.getRole().getId(), principal);

        // Desativar usuário
        user.setActive(false);
        user.setEndDate(LocalDate.now());
        user.setUpdatedAt(Instant.now());

        companyUserRepository.save(user);

        // Revogar todas as sessões ativas
        revokeAllUserSessions(user.getIdentity().getId());

        log.info("Usuário desativado com sucesso: id={}", id);
    }

    // ========== Métodos de Validação ==========

    private void validateEmailUniqueness(String email) {
        if (identityRepository.existsByEmail(email)) {
            throw new ValidationException("Este email já está em uso");
        }
    }

    private void validateCpfUniqueness(String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        if (personRepository.existsByCpf(cleanCpf)) {
            throw new ValidationException("Este CPF já está cadastrado");
        }
    }

    private void validatePasswordRequirements(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Senha deve ter no mínimo 8 caracteres");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Senha deve conter pelo menos uma letra minúscula");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Senha deve conter pelo menos uma letra maiúscula");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Senha deve conter pelo menos um número");
        }
    }

    private void validateRole(Long roleId, UserPrincipal principal) {
        CustomRole role = customRoleRepository.findById(roleId)
                .orElseThrow(() -> new ValidationException("Role não encontrada"));

        if (!role.getCompany().getId().equals(principal.getCompanyId())) {
            throw new ValidationException("Role não pertence à empresa do usuário");
        }

        // Validar hierarquia - usuário não pode atribuir role superior à sua
        if (principal.getRoleId() != null) {
            CustomRole userRole = customRoleRepository.findById(principal.getRoleId())
                    .orElseThrow(() -> new ValidationException("Role do usuário não encontrada"));

            if (role.getHierarchyLevel() < userRole.getHierarchyLevel()) {
                throw new ValidationException(
                        "Você não pode atribuir uma role de nível hierárquico superior ao seu");
            }
        }
    }

    private void validateAllowedComplexes(List<Long> complexIds, Long companyId, UserPrincipal principal) {
        if (complexIds == null || complexIds.isEmpty()) {
            throw new ValidationException("Usuário deve ter acesso a pelo menos um complexo");
        }

        // Validar que todos os complexos existem e pertencem à empresa
        for (Long complexId : complexIds) {
            String query = "SELECT COUNT(*) FROM operations.cinema_complexes " +
                    "WHERE id = ? AND company_id = ? AND active = true";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, complexId, companyId);

            if (count == null || count == 0) {
                throw new ValidationException(
                        "Complexo ID " + complexId + " não existe ou não pertence à empresa");
            }
        }

        // Validar que usuário tem acesso aos complexos que está tentando atribuir
        if (principal.getAllowedComplexes() != null && !principal.getAllowedComplexes().isEmpty()) {
            for (Long complexId : complexIds) {
                if (!principal.getAllowedComplexes().contains(complexId)) {
                    throw new ValidationException(
                            "Você não pode atribuir acesso ao complexo ID " + complexId +
                                    " pois você não tem acesso a ele");
                }
            }
        }
    }

    private void validateRoleHierarchy(Long targetRoleId, UserPrincipal principal) {
        if (principal.getRoleId() == null) {
            return; // Admin sem role específica pode tudo
        }

        CustomRole targetRole = customRoleRepository.findById(targetRoleId)
                .orElseThrow(() -> new ValidationException("Role não encontrada"));
        CustomRole userRole = customRoleRepository.findById(principal.getRoleId())
                .orElseThrow(() -> new ValidationException("Role do usuário não encontrada"));

        if (targetRole.getHierarchyLevel() < userRole.getHierarchyLevel()) {
            throw new ValidationException(
                    "Você não pode modificar usuários com nível hierárquico superior ao seu");
        }
    }

    // ========== Métodos de Criação ==========

    private Person createPerson(CreateUserRequest request) {
        Person person = new Person();
        person.setFullName(request.fullName());
        person.setEmail(request.email());

        if (request.cpf() != null) {
            person.setCpf(request.cpf().replaceAll("[^0-9]", ""));
        }

        person.setBirthDate(request.birthDate());
        person.setPhone(request.phone());
        person.setMobile(request.mobile());

        // Endereço
        person.setZipCode(request.zipCode());
        person.setStreetAddress(request.streetAddress());
        person.setAddressNumber(request.addressNumber());
        person.setAddressComplement(request.addressComplement());
        person.setNeighborhood(request.neighborhood());
        person.setCity(request.city());
        person.setState(request.state());
        person.setCountry(request.country() != null ? request.country() : "BR");

        Instant now = Instant.now();
        person.setCreatedAt(now);
        person.setUpdatedAt(now);

        return person;
    }

    private Identity createIdentity(CreateUserRequest request, Person person) {
        Identity identity = new Identity();
        identity.setPerson(person);
        identity.setEmail(request.email());
        identity.setPasswordHash(passwordEncoder.encode(request.password()));
        identity.setIdentityType(IdentityType.EMPLOYEE);
        identity.setActive(request.active());
        identity.setEmailVerified(true); // Employee accounts are pre-verified

        Instant now = Instant.now();
        identity.setCreatedAt(now);
        identity.setUpdatedAt(now);
        identity.setPasswordChangedAt(now);

        return identity;
    }

    private CompanyUser createCompanyUser(CreateUserRequest request, Identity identity,
                                          Long employeeId, UserPrincipal principal) {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setIdentity(identity);

        Company company = companyRepository.findById(principal.getCompanyId())
                .orElseThrow(() -> new ValidationException("Empresa não encontrada"));
        companyUser.setCompany(company);

        companyUser.setEmployeeId(employeeId);

        CustomRole role = customRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new ValidationException("Role não encontrada"));
        companyUser.setRole(role);

        companyUser.setDepartment(request.department());
        companyUser.setJobLevel(request.jobLevel());
        companyUser.setLocation(request.location());
        companyUser.setActive(request.active());
        companyUser.setStartDate(request.startDate() != null ? request.startDate() : LocalDate.now());

        // Converter allowed_complexes para JSON array string
        companyUser.setAllowedComplexes(convertComplexListToJson(request.allowedComplexes()));

        companyUser.setAssignedBy(principal.getEmail());
        Instant now = Instant.now();
        companyUser.setAssignedAt(now);
        companyUser.setCreatedAt(now);
        companyUser.setUpdatedAt(now);

        return companyUser;
    }

    // ========== Métodos de Atualização ==========

    private boolean hasPersonUpdates(UpdateUserRequest request) {
        return request.fullName() != null || request.cpf() != null ||
                request.birthDate() != null || request.phone() != null ||
                request.mobile() != null || request.zipCode() != null ||
                request.streetAddress() != null || request.addressNumber() != null ||
                request.addressComplement() != null || request.neighborhood() != null ||
                request.city() != null || request.state() != null || request.country() != null;
    }

    private boolean hasIdentityUpdates(UpdateUserRequest request) {
        return request.email() != null || request.password() != null;
    }

    private void updatePerson(Person person, UpdateUserRequest request) {
        if (request.fullName() != null)
            person.setFullName(request.fullName());
        if (request.cpf() != null)
            person.setCpf(request.cpf().replaceAll("[^0-9]", ""));
        if (request.birthDate() != null)
            person.setBirthDate(request.birthDate());
        if (request.phone() != null)
            person.setPhone(request.phone());
        if (request.mobile() != null)
            person.setMobile(request.mobile());
        if (request.zipCode() != null)
            person.setZipCode(request.zipCode());
        if (request.streetAddress() != null)
            person.setStreetAddress(request.streetAddress());
        if (request.addressNumber() != null)
            person.setAddressNumber(request.addressNumber());
        if (request.addressComplement() != null)
            person.setAddressComplement(request.addressComplement());
        if (request.neighborhood() != null)
            person.setNeighborhood(request.neighborhood());
        if (request.city() != null)
            person.setCity(request.city());
        if (request.state() != null)
            person.setState(request.state());
        if (request.country() != null)
            person.setCountry(request.country());

        person.setUpdatedAt(Instant.now());
        personRepository.save(person);
    }

    private void updateIdentity(Identity identity, UpdateUserRequest request) {
        if (request.email() != null && !request.email().equals(identity.getEmail())) {
            validateEmailUniqueness(request.email());
            identity.setEmail(request.email());
        }

        if (request.password() != null) {
            validatePasswordRequirements(request.password());
            identity.setPasswordHash(passwordEncoder.encode(request.password()));
            identity.setPasswordChangedAt(Instant.now());
        }

        identity.setUpdatedAt(Instant.now());
        identityRepository.save(identity);
    }

    private void updateCompanyUser(CompanyUser user, UpdateUserRequest request, UserPrincipal principal) {
        if (request.roleId() != null) {
            validateRole(request.roleId(), principal);
            CustomRole role = customRoleRepository.findById(request.roleId())
                    .orElseThrow(() -> new ValidationException("Role não encontrada"));
            user.setRole(role);
        }

        if (request.active() != null)
            user.setActive(request.active());
        if (request.department() != null)
            user.setDepartment(request.department());
        if (request.jobLevel() != null)
            user.setJobLevel(request.jobLevel());
        if (request.location() != null)
            user.setLocation(request.location());
        if (request.endDate() != null)
            user.setEndDate(request.endDate());

        if (request.allowedComplexes() != null) {
            validateAllowedComplexes(request.allowedComplexes(), principal.getCompanyId(), principal);
            user.setAllowedComplexes(convertComplexListToJson(request.allowedComplexes()));
        }

        user.setUpdatedAt(Instant.now());
    }

    // ========== Métodos Auxiliares ==========

    private Long generateNextEmployeeId(Long companyId) {
        String sql = "SELECT get_next_employee_id(?)";
        return jdbcTemplate.queryForObject(sql, Long.class, companyId);
    }

    private void revokeAllUserSessions(Long identityId) {
        List<UserSession> sessions = userSessionRepository.findByIdentity_IdAndActiveTrue(identityId);
        Instant now = Instant.now();

        for (UserSession session : sessions) {
            session.setActive(false);
            session.setRevoked(true);
            session.setRevokedAt(now);
        }

        userSessionRepository.saveAll(sessions);
        log.debug("Revogadas {} sessões do usuário identityId={}", sessions.size(), identityId);
    }

    private String convertComplexListToJson(List<Long> complexIds) {
        try {
            return jsonMapper.writeValueAsString(complexIds);
        } catch (JacksonException e) {
            log.error("Erro ao converter lista de complexos para JSON", e);
            throw new ValidationException("Erro ao processar lista de complexos");
        }
    }

    private List<Long> parseComplexesFromJson(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return List.of();
        }

        try {
            return jsonMapper.readValue(json,
                    jsonMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (JacksonException e) {
            log.error("Erro ao parsear JSON de complexos: {}", json, e);
            return List.of();
        }
    }

    private UserResponse toUserResponse(CompanyUser user) {
        Person person = user.getIdentity().getPerson();
        Identity identity = user.getIdentity();
        CustomRole role = user.getRole();

        // Buscar informações dos complexos
        List<Long> complexIds = parseComplexesFromJson(user.getAllowedComplexes());
        List<UserResponse.ComplexInfo> complexInfos = getComplexInfos(complexIds);

        return new UserResponse(
                user.getId(),
                user.getEmployeeId(),
                identity.getId(),
                person.getFullName(),
                identity.getEmail(),
                person.getCpf(),
                person.getBirthDate(),
                person.getPhone(),
                person.getMobile(),
                person.getZipCode(),
                person.getStreetAddress(),
                person.getAddressNumber(),
                person.getAddressComplement(),
                person.getNeighborhood(),
                person.getCity(),
                person.getState(),
                person.getCountry(),
                new UserResponse.RoleInfo(
                        role.getId(),
                        role.getName(),
                        role.getHierarchyLevel()),
                user.getDepartment(),
                user.getJobLevel(),
                user.getLocation(),
                complexInfos,
                user.getActive(),
                user.getStartDate(),
                user.getEndDate(),
                user.getAssignedBy(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastAccess(),
                user.getAccessCount());
    }

    private List<UserResponse.ComplexInfo> getComplexInfos(List<Long> complexIds) {
        if (complexIds.isEmpty()) {
            return List.of();
        }

        String sql = "SELECT id, name, code FROM operations.cinema_complexes WHERE id = ANY(?)";

        try {
            return jdbcTemplate.query(
                    sql,
                    ps -> ps.setArray(1, ps.getConnection().createArrayOf("BIGINT", complexIds.toArray())),
                    (rs, rowNum) -> new UserResponse.ComplexInfo(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("code")));
        } catch (Exception e) {
            log.error("Erro ao buscar informações dos complexos", e);
            return new ArrayList<>();
        }
    }
}
