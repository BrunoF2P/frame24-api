package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.SessionSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionSeatStatusRepository extends JpaRepository<SessionSeatStatus, Long> {

    List<SessionSeatStatus> findByShowtime_Id(Long showtimeId);

    Optional<SessionSeatStatus> findByShowtime_IdAndSeat_Id(Long showtimeId, Long seatId);

    List<SessionSeatStatus> findByReservationUuid(String reservationUuid);

    Optional<SessionSeatStatus> findBySaleId(Long saleId);

    @Query("SELECT sss FROM SessionSeatStatus sss WHERE sss.showtime.id = :showtimeId " +
            "AND sss.expirationDate < :now AND sss.reservationUuid IS NOT NULL")
    List<SessionSeatStatus> findExpiredReservations(@Param("showtimeId") Long showtimeId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM SessionSeatStatus sss WHERE sss.reservationUuid = :reservationUuid")
    void deleteByReservationUuid(@Param("reservationUuid") String reservationUuid);

    @Modifying
    @Query("DELETE FROM SessionSeatStatus sss WHERE sss.expirationDate < :now AND sss.reservationUuid IS NOT NULL AND sss.saleId IS NULL")
    void deleteExpiredReservations(@Param("now") Instant now);
}
