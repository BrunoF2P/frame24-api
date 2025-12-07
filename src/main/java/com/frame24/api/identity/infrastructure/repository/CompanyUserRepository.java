package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.CompanyUser;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    Page<CompanyUser> findByCompanyId(@NonNull Long companyId, @NonNull Pageable pageable);

    /**
     * Busca todos os CompanyUser associados a uma Identity.
     *
     * @param identityId ID da identity
     * @return Lista de CompanyUser
     */
    List<CompanyUser> findByIdentityId(@NonNull Long identityId);

    Optional<CompanyUser> findByCompanyIdAndIdentityId(@NonNull Long companyId, @NonNull Long identityId);

    boolean existsByCompanyIdAndIdentityId(@NonNull Long companyId, @NonNull Long identityId);

    List<CompanyUser> findByCompanyIdAndActiveTrue(@NonNull Long companyId);

    Optional<CompanyUser> findByEmployeeId(@NonNull Long employeeId);

    /**
     * Busca usuário por employee_id dentro da empresa.
     *
     * @param employeeId ID do funcionário
     * @param companyId  ID da empresa
     * @return CompanyUser se encontrado
     */
    Optional<CompanyUser> findByEmployeeIdAndCompanyId(@NonNull Long employeeId, @NonNull Long companyId);

    /**
     * Verifica se existe usuário com o employee_id na empresa.
     *
     * @param employeeId ID do funcionário
     * @param companyId  ID da empresa
     * @return true se existe
     */
    boolean existsByEmployeeIdAndCompanyId(@NonNull Long employeeId, @NonNull Long companyId);

    /**
     * Busca usuários com filtros dinâmicos.
     * A query usa JPQL para suportar busca parcial em múltiplos campos.
     *
     * @param companyId    ID da empresa
     * @param employeeId   ID do funcionário (exato, opcional)
     * @param emailPattern Email pattern (parcial, opcional)
     * @param namePattern  Nome pattern (parcial, opcional)
     * @param department   Departamento (exato, opcional)
     * @param active       Status ativo (opcional)
     * @param roleId       ID da role (opcional)
     * @param pageable     Paginação
     * @return Página de usuários
     */
    @org.springframework.data.jpa.repository.Query("""
            SELECT cu FROM CompanyUser cu
            JOIN FETCH cu.identity i
            JOIN FETCH i.person p
            JOIN FETCH cu.role r
            WHERE cu.company.id = :companyId
            AND (:employeeId IS NULL OR cu.employeeId = :employeeId)
            AND (:emailPattern IS NULL OR LOWER(i.email) LIKE LOWER(CONCAT('%', :emailPattern, '%')))
            AND (:namePattern IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :namePattern, '%')))
            AND (:department IS NULL OR cu.department = :department)
            AND (:active IS NULL OR cu.active = :active)
            AND (:roleId IS NULL OR cu.role.id = :roleId)
            """)
    Page<CompanyUser> searchUsers(
            @NonNull Long companyId,
            Long employeeId,
            String emailPattern,
            String namePattern,
            String department,
            Boolean active,
            Long roleId,
            @NonNull Pageable pageable);

    /**
     * Busca CompanyUser por ID com todas as relações carregadas (EAGER).
     *
     * @param id ID do CompanyUser
     * @return CompanyUser com relações
     */
    @org.springframework.data.jpa.repository.Query("""
            SELECT cu FROM CompanyUser cu
            JOIN FETCH cu.identity i
            JOIN FETCH i.person p
            JOIN FETCH cu.role r
            WHERE cu.id = :id
            """)
    Optional<CompanyUser> findByIdWithRelations(@NonNull Long id);
}
