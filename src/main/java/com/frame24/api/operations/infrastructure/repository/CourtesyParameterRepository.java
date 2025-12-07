package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.CourtesyParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtesyParameterRepository extends JpaRepository<CourtesyParameter, Long> {

    List<CourtesyParameter> findByCinemaComplexId(Long cinemaComplexId);

    @Query("SELECT cp FROM CourtesyParameter cp WHERE cp.cinemaComplexId = :cinemaComplexId " +
            "AND cp.validityStart <= :date " +
            "AND (cp.validityEnd IS NULL OR cp.validityEnd >= :date)")
    Optional<CourtesyParameter> findActiveByCinemaComplexIdAndDate(
            @Param("cinemaComplexId") Long cinemaComplexId,
            @Param("date") LocalDate date);
}
