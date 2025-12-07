package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.ShowtimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeScheduleRepository extends JpaRepository<ShowtimeSchedule, Long> {

    List<ShowtimeSchedule> findByCinemaComplex_Id(Long cinemaComplexId);

    List<ShowtimeSchedule> findByRoom_Id(Long roomId);

    List<ShowtimeSchedule> findByMovieId(Long movieId);

    @Query("SELECT ss FROM ShowtimeSchedule ss WHERE ss.cinemaComplex.id = :cinemaComplexId " +
            "AND ss.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY ss.startTime")
    List<ShowtimeSchedule> findByCinemaComplexAndDateRange(
            @Param("cinemaComplexId") Long cinemaComplexId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT ss FROM ShowtimeSchedule ss WHERE ss.movieId = :movieId " +
            "AND ss.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY ss.startTime")
    List<ShowtimeSchedule> findByMovieAndDateRange(
            @Param("movieId") Long movieId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT ss FROM ShowtimeSchedule ss WHERE ss.room.id = :roomId " +
            "AND ((ss.startTime BETWEEN :startTime AND :endTime) " +
            "OR (ss.endTime BETWEEN :startTime AND :endTime) " +
            "OR (ss.startTime <= :startTime AND ss.endTime >= :endTime))")
    List<ShowtimeSchedule> findConflictingShowtimes(
            @Param("roomId") Long roomId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    @Query("SELECT ss FROM ShowtimeSchedule ss WHERE ss.cinemaComplex.companyId = :companyId " +
            "AND ss.startTime >= :startDate " +
            "ORDER BY ss.startTime")
    List<ShowtimeSchedule> findUpcomingByCompanyId(
            @Param("companyId") Long companyId,
            @Param("startDate") Instant startDate);
}
