package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByCompanyId(Long companyId);

    Optional<Permission> findByCompanyIdAndCode(Long companyId, String code);

    List<Permission> findByCompanyIdAndModule(Long companyId, String module);

    List<Permission> findByCompanyIdAndResource(Long companyId, String resource);

    List<Permission> findByCompanyIdAndActiveTrue(Long companyId);
}
