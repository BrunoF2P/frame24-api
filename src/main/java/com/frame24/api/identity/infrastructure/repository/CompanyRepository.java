package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCnpj(String cnpj);

    Optional<Company> findByTenantSlug(String tenantSlug);

    boolean existsByCnpj(String cnpj);

    boolean existsByTenantSlug(String tenantSlug);
}
