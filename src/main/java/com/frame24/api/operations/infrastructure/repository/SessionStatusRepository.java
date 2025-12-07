package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionStatusRepository extends JpaRepository<SessionStatus, Long> {

    List<SessionStatus> findByCompanyId(Long companyId);

    Optional<SessionStatus> findByCompanyIdAndName(Long companyId, String name);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
