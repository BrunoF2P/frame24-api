package com.frame24.api.operations.infrastructure.repository;

import com.frame24.api.operations.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByCinemaComplex_Id(Long cinemaComplexId);

    List<Room> findByCinemaComplex_IdAndActiveTrue(Long cinemaComplexId);

    Optional<Room> findByCinemaComplex_IdAndRoomNumber(Long cinemaComplexId, String roomNumber);

    boolean existsByCinemaComplex_IdAndRoomNumber(Long cinemaComplexId, String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.cinemaComplex.companyId = :companyId")
    List<Room> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Room r WHERE r.cinemaComplex.companyId = :companyId AND r.active = true")
    List<Room> findByCompanyIdAndActiveTrue(@Param("companyId") Long companyId);
}
