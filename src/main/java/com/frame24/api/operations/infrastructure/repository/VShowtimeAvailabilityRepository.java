package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.VShowtimeAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VShowtimeAvailabilityRepository extends JpaRepository<VShowtimeAvailability, Long> {

    List<VShowtimeAvailability> findByCompanyId(Long companyId);

    List<VShowtimeAvailability> findByCinemaComplexId(Long cinemaComplexId);

    List<VShowtimeAvailability> findByMovieId(Long movieId);

    @Query("SELECT v FROM VShowtimeAvailability v WHERE v.companyId = :companyId " +
            "AND v.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY v.startTime")
    List<VShowtimeAvailability> findByCompanyIdAndDateRange(
            @Param("companyId") Long companyId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT v FROM VShowtimeAvailability v WHERE v.cinemaComplexId = :cinemaComplexId " +
            "AND v.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY v.startTime")
    List<VShowtimeAvailability> findByCinemaComplexIdAndDateRange(
            @Param("cinemaComplexId") Long cinemaComplexId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT v FROM VShowtimeAvailability v WHERE v.movieId = :movieId " +
            "AND v.startTime >= :startDate " +
            "ORDER BY v.startTime")
    List<VShowtimeAvailability> findUpcomingByMovieId(
            @Param("movieId") Long movieId,
            @Param("startDate") Instant startDate);

    @Query("SELECT v FROM VShowtimeAvailability v WHERE v.sessionStatus = :status " +
            "AND v.companyId = :companyId")
    List<VShowtimeAvailability> findByCompanyIdAndSessionStatus(
            @Param("companyId") Long companyId,
            @Param("status") String status);
}
