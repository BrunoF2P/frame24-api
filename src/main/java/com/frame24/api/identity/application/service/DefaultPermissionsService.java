package com.frame24.api.identity.application.service;

import com.frame24.api.identity.domain.Company;
import com.frame24.api.identity.domain.Permission;
import com.frame24.api.identity.domain.enums.CompanyPlanType;
import com.frame24.api.identity.domain.enums.SystemPermission;
import com.frame24.api.identity.infrastructure.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serviço responsável por sincronizar as permissões de uma empresa com seu
 * plano de assinatura.
 */
@Service
@RequiredArgsConstructor
public class DefaultPermissionsService {

    private static final Logger log = LoggerFactory.getLogger(DefaultPermissionsService.class);

    private final PermissionRepository permissionRepository;

    /**
     * Sincroniza as permissões da empresa baseada no plano.
     * <p>
     * - Cria novas permissões que o plano oferece.
     * - Desativa permissões que o plano NÃO oferece mais (downgrade).
     * - Reativa permissões que voltaram a ser permitidas (upgrade).
     *
     * @param company empresa alvo
     * @param plan    plano atual da empresa
     */
    @Transactional
    public void syncPermissions(Company company, CompanyPlanType plan) {
        log.info("Sincronizando permissões para empresa {} com plano {}", company.getCorporateName(), plan);

        Set<SystemPermission> planPermissions = plan.getPermissions();

        // Busca permissões existentes no banco para esta empresa
        Map<String, Permission> existingPermissionsMap = permissionRepository.findByCompanyId(company.getId())
                .stream()
                .collect(Collectors.toMap(Permission::getCode, Function.identity()));

        // 1. Processar permissões do plano (Criar ou Atualizar)
        for (SystemPermission sysPerm : planPermissions) {
            Permission permission = existingPermissionsMap.get(sysPerm.getCode());

            if (permission == null) {
                // Cria nova
                permission = new Permission();
                permission.setCompany(company);
                permission.setResource(sysPerm.getResource());
                permission.setAction(sysPerm.getAction());
                permission.setCode(sysPerm.getCode());
                permission.setName(sysPerm.getName());
                permission.setDescription(sysPerm.getDescription());
                permission.setModule(sysPerm.getModule());
                permission.setActive(true);
                permission.setCreatedAt(Instant.now());

                permissionRepository.save(permission);
                log.debug("Permissão criada: {}", sysPerm.getCode());
            } else {
                // Atualiza existente (se estiver inativa, reativa)
                if (!Boolean.TRUE.equals(permission.getActive())) {
                    permission.setActive(true);
                    permissionRepository.save(permission);
                    log.debug("Permissão reativada: {}", sysPerm.getCode());
                }
                // Também atualiza metadados se mudou no sistema (nome/descrição)
                // Isso garante que mudanças no Enum reflitam no banco
                if (!permission.getName().equals(sysPerm.getName()) ||
                        !permission.getDescription().equals(sysPerm.getDescription())) {
                    permission.setName(sysPerm.getName());
                    permission.setDescription(sysPerm.getDescription());
                    permissionRepository.save(permission);
                }
            }
        }

        // 2. Processar permissões que NÃO estão no plano (Desativar)
        for (Permission existingPerm : existingPermissionsMap.values()) {
            boolean isInPlan = planPermissions.stream()
                    .anyMatch(p -> p.getCode().equals(existingPerm.getCode()));

            if (!isInPlan && Boolean.TRUE.equals(existingPerm.getActive())) {
                existingPerm.setActive(false);
                permissionRepository.save(existingPerm);
                log.info("Permissão desativada (fora do plano): {}", existingPerm.getCode());

                // Nota: Opcionalmente poderíamos remover de roles aqui,
                // mas desativar já impede que seja listada no endpoint /permissions
                // Porem, se a validação for apenas por existência do relacionamento
                // role_permission,
                // pode ser necessário remover da role.
                // Mas por enquanto vamos confiar no status Active.
            }
        }
    }
}
