package com.frame24.api.identity.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.identity.application.dto.CreateRoleRequest;
import com.frame24.api.identity.application.dto.RoleResponse;
import com.frame24.api.identity.application.dto.UpdateRoleRequest;
import com.frame24.api.identity.domain.Company;
import com.frame24.api.identity.domain.CustomRole;
import com.frame24.api.identity.domain.Permission;
import com.frame24.api.identity.domain.RolePermission;
import com.frame24.api.identity.infrastructure.repository.CompanyRepository;
import com.frame24.api.identity.infrastructure.repository.CustomRoleRepository;
import com.frame24.api.identity.infrastructure.repository.PermissionRepository;
import com.frame24.api.identity.infrastructure.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de roles customizadas.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final CustomRoleRepository customRoleRepository;
    private final CompanyRepository companyRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    /**
     * Cria uma nova role customizada.
     *
     * @param request   dados da role
     * @param principal usuário autenticado
     * @return role criada
     */
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request, UserPrincipal principal) {
        Long companyId = principal.getCompanyId();

        // Verifica se já existe uma role com o mesmo nome
        customRoleRepository.findByCompanyIdAndName(companyId, request.name())
                .ifPresent(role -> {
                    throw new IllegalArgumentException("Já existe uma role com este nome");
                });

        // Busca a empresa
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        CustomRole role = new CustomRole();
        role.setCompany(company);
        role.setName(request.name());
        role.setDescription(request.description());
        role.setHierarchyLevel(request.hierarchyLevel());
        role.setIsSystemRole(false);
        role.setCreatedAt(Instant.now());

        CustomRole savedRole = customRoleRepository.save(role);

        // Associa permissões se houver
        if (request.permissions() != null && !request.permissions().isEmpty()) {
            addPermissionsToRole(savedRole, request.permissions(), companyId);
        }

        return toResponse(savedRole);
    }

    /**
     * Lista todas as roles da empresa.
     *
     * @param principal usuário autenticado
     * @return lista de roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles(UserPrincipal principal) {
        Long companyId = principal.getCompanyId();

        return customRoleRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma role por ID.
     *
     * @param id        ID da role
     * @param principal usuário autenticado
     * @return role encontrada
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id, UserPrincipal principal) {
        Long companyId = principal.getCompanyId();

        CustomRole role = customRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role não encontrada"));

        // Verifica se a role pertence à empresa do usuário
        if (!role.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("Acesso negado a esta role");
        }

        return toResponse(role);
    }

    /**
     * Atualiza uma role.
     *
     * @param id        ID da role
     * @param request   dados para atualização
     * @param principal usuário autenticado
     * @return role atualizada
     */
    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request, UserPrincipal principal) {
        Long companyId = principal.getCompanyId();

        CustomRole role = customRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role não encontrada"));

        // Verifica se a role pertence à empresa do usuário
        if (!role.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("Acesso negado a esta role");
        }

        // Não permite atualizar roles do sistema
        if (role.getIsSystemRole()) {
            throw new IllegalArgumentException("Não é possível atualizar roles do sistema");
        }

        // Verifica se o novo nome já existe (se foi alterado)
        if (request.name() != null && !request.name().equals(role.getName())) {
            customRoleRepository.findByCompanyIdAndName(companyId, request.name())
                    .ifPresent(existingRole -> {
                        throw new IllegalArgumentException("Já existe uma role com este nome");
                    });
        }

        // Atualiza os campos básicos
        if (request.name() != null) {
            role.setName(request.name());
        }
        if (request.description() != null) {
            role.setDescription(request.description());
        }
        if (request.hierarchyLevel() != null) {
            role.setHierarchyLevel(request.hierarchyLevel());
        }

        // Atualiza permissões se fornecido
        if (request.permissions() != null) {
            // Remove permissões atuais (busca todas as rolePermissions desta role e deleta)
            // Obs: role.getRolePermissions() pode estar desatualizado ou ser lazy, melhor
            // buscar via repo se necessário
            // ou usar cascade se configurado. Aqui faremos manual para segurança.
            List<RolePermission> currentPermissions = rolePermissionRepository.findByRoleId(role.getId());
            rolePermissionRepository.deleteAll(currentPermissions);
            rolePermissionRepository.flush(); // Garante deleção antes de inserir novas

            // Adiciona novas
            if (!request.permissions().isEmpty()) {
                addPermissionsToRole(role, request.permissions(), companyId);
            }
        }

        role.setUpdatedAt(Instant.now());

        CustomRole updatedRole = customRoleRepository.save(role);

        // Força refresh para carregar permissões atualizadas se necessário,
        // mas como já temos as permissões inseridas, podemos só retornar.
        // No entanto, toResponse usa role.getRolePermissions which is a Set.
        // É melhor recarregar a role do banco para garantir consistência do Set ou
        // atualizar o objeto manualmente.
        // Vamos recarregar:
        updatedRole = customRoleRepository.findById(id).orElseThrow();

        return toResponse(updatedRole);
    }

    /**
     * Deleta uma role.
     *
     * @param id        ID da role
     * @param principal usuário autenticado
     */
    @Transactional
    public void deleteRole(Long id, UserPrincipal principal) {
        Long companyId = principal.getCompanyId();

        CustomRole role = customRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role não encontrada"));

        // Verifica se a role pertence à empresa do usuário
        if (!role.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("Acesso negado a esta role");
        }

        // Não permite deletar roles do sistema
        if (role.getIsSystemRole()) {
            throw new IllegalArgumentException("Não é possível deletar roles do sistema");
        }

        // Verifica se existem usuários usando esta role
        if (!role.getCompanyUsers().isEmpty()) {
            throw new IllegalArgumentException("Não é possível deletar uma role que está sendo usada por usuários");
        }

        // Remove as permissões associadas antes de remover a role
        List<RolePermission> permissions = rolePermissionRepository.findByRoleId(role.getId());
        rolePermissionRepository.deleteAll(permissions);

        customRoleRepository.delete(role);
    }

    private void addPermissionsToRole(CustomRole role, Set<String> permissionCodes, Long companyId) {
        for (String code : permissionCodes) {
            Permission permission = permissionRepository.findByCompanyIdAndCode(companyId, code)
                    .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada ou inválida: " + code));

            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setGrantedAt(Instant.now());

            rolePermissionRepository.save(rolePermission);
        }
    }

    /**
     * Converte uma entidade CustomRole para RoleResponse.
     *
     * @param role entidade
     * @return DTO de resposta
     */
    private RoleResponse toResponse(CustomRole role) {
        Set<String> permissions = new HashSet<>();
        if (role.getRolePermissions() != null) {
            permissions = role.getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getCode())
                    .collect(Collectors.toSet());
        }

        return new RoleResponse(
                role.getId(),
                role.getCompany().getId(),
                role.getName(),
                role.getDescription(),
                role.getIsSystemRole(),
                role.getHierarchyLevel(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                permissions);
    }
}
