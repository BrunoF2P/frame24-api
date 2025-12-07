package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.ProjectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectionTypeRepository extends JpaRepository<ProjectionType, Long> {

    List<ProjectionType> findByCompanyId(Long companyId);

    Optional<ProjectionType> findByCompanyIdAndName(Long companyId, String name);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
