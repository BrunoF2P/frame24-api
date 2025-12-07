package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.CustomRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomRoleRepository extends JpaRepository<CustomRole, Long> {

    List<CustomRole> findByCompanyId(Long companyId);

    Optional<CustomRole> findByCompanyIdAndName(Long companyId, String name);

    List<CustomRole> findByCompanyIdAndIsSystemRoleTrue(Long companyId);

    List<CustomRole> findByCompanyIdAndIsSystemRoleFalse(Long companyId);
}
