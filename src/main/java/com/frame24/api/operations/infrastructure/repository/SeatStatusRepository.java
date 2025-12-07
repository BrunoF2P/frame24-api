package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Long> {

    List<SeatStatus> findByCompanyId(Long companyId);

    Optional<SeatStatus> findByCompanyIdAndName(Long companyId, String name);

    Optional<SeatStatus> findByCompanyIdAndIsDefaultTrue(Long companyId);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
