package com.frame24.api.identity.application.service;

import com.frame24.api.identity.domain.Company;
import com.frame24.api.identity.domain.CustomRole;
import com.frame24.api.identity.domain.Permission;
import com.frame24.api.identity.domain.RolePermission;
import com.frame24.api.identity.infrastructure.repository.CustomRoleRepository;
import com.frame24.api.identity.infrastructure.repository.PermissionRepository;
import com.frame24.api.identity.infrastructure.repository.RolePermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Serviço para criação de roles padrão do sistema.
 */
@Service
public class DefaultRolesService {

    private static final Logger log = LoggerFactory.getLogger(DefaultRolesService.class);

    private final CustomRoleRepository customRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    /**
     * Definição das roles padrão do sistema.
     */
    private static final List<RoleDefinition> DEFAULT_ROLES = List.of(
            new RoleDefinition("Administrador", "Acesso administrativo completo ao tenant.", 2),
            new RoleDefinition("Gerente", "Gerenciamento de operações e relatórios.", 3),
            new RoleDefinition("Operador", "Operações diárias (vendas, sessões).", 4),
            new RoleDefinition("Visualizador", "Apenas visualização de dados.", 5));

    public DefaultRolesService(CustomRoleRepository customRoleRepository,
                               PermissionRepository permissionRepository,
                               RolePermissionRepository rolePermissionRepository) {
        this.customRoleRepository = customRoleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    /**
     * Cria as roles padrão do sistema para uma empresa.
     *
     * @param company empresa para a qual criar as roles
     */
    @Transactional
    public void createDefaultRoles(Company company) {
        log.info("Criando roles padrão para empresa: {}", company.getCorporateName());

        List<Permission> allPermissions = permissionRepository.findByCompanyIdAndActiveTrue(company.getId());

        for (RoleDefinition def : DEFAULT_ROLES) {
            CustomRole role = new CustomRole();
            role.setCompany(company);
            role.setName(def.name());
            role.setDescription(def.description());
            role.setHierarchyLevel(def.hierarchyLevel());
            role.setIsSystemRole(true);
            role.setCreatedAt(Instant.now());

            CustomRole savedRole = customRoleRepository.save(role);

            // Se for Administrador, atribui todas as permissões disponíveis
            if ("Administrador".equals(def.name())) {
                assignAllPermissions(savedRole, allPermissions);
            }

            log.debug("Role criada: {} (hierarchy={})", def.name(), def.hierarchyLevel());
        }

        log.info("{} roles padrão criadas para empresa {}", DEFAULT_ROLES.size(), company.getId());
    }

    private void assignAllPermissions(CustomRole role, List<Permission> permissions) {
        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setGrantedAt(Instant.now());
            rolePermission.setGrantedBy("SYSTEM");

            rolePermissionRepository.save(rolePermission);
        }
    }

    private record RoleDefinition(String name, String description, int hierarchyLevel) {
    }
}
