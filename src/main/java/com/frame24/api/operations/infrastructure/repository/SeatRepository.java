package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRoom_Id(Long roomId);

    List<Seat> findByRoom_IdAndActiveTrue(Long roomId);

    Optional<Seat> findByRoom_IdAndSeatCode(Long roomId, String seatCode);

    boolean existsByRoom_IdAndSeatCode(Long roomId, String seatCode);

    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId ORDER BY s.rowCode, s.columnNumber")
    List<Seat> findByRoomIdOrderByRowAndColumn(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.room.id = :roomId AND s.active = true")
    Integer countActiveSeatsByRoomId(@Param("roomId") Long roomId);
}
