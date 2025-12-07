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
}
