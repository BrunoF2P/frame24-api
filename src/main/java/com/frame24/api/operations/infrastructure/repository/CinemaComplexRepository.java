package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.CinemaComplex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaComplexRepository extends JpaRepository<CinemaComplex, Long> {

    List<CinemaComplex> findByCompanyId(Long companyId);

    List<CinemaComplex> findByCompanyIdAndActiveTrue(Long companyId);

    Optional<CinemaComplex> findByCompanyIdAndCode(Long companyId, String code);

    Optional<CinemaComplex> findByCnpj(String cnpj);

    boolean existsByCompanyIdAndCode(Long companyId, String code);

    boolean existsByCnpj(String cnpj);
}
