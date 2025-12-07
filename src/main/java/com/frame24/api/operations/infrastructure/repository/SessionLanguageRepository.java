package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.SessionLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionLanguageRepository extends JpaRepository<SessionLanguage, Long> {

    List<SessionLanguage> findByCompanyId(Long companyId);

    Optional<SessionLanguage> findByCompanyIdAndName(Long companyId, String name);

    Optional<SessionLanguage> findByCompanyIdAndAbbreviation(Long companyId, String abbreviation);

    boolean existsByCompanyIdAndName(Long companyId, String name);

    boolean existsByCompanyIdAndAbbreviation(Long companyId, String abbreviation);
}
