package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.AudioType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioTypeRepository extends JpaRepository<AudioType, Long> {

    List<AudioType> findByCompanyId(Long companyId);

    Optional<AudioType> findByCompanyIdAndName(Long companyId, String name);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
