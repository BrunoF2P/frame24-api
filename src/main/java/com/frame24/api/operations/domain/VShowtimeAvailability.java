package com.frame24.api.operations.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "v_showtime_availability", schema = "operations")
public class VShowtimeAvailability {
    @Id
    @Column(name = "showtime_id")
    private Long showtimeId;

    @Column(name = "cinema_complex_id")
    private Long cinemaComplexId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "room_id")
    private Long roomId;

    @Size(max = 100)
    @Column(name = "room_name", length = 100)
    private String roomName;

    @Column(name = "movie_id")
    private Long movieId;

    @Size(max = 300)
    @Column(name = "movie_title", length = 300)
    private String movieTitle;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "total_seats")
    private Long totalSeats;

    @Column(name = "seats_sold")
    private Long seatsSold;

    @Column(name = "seats_reserved")
    private Long seatsReserved;

    @Column(name = "seats_available")
    private Long seatsAvailable;

    @Column(name = "occupancy_percentage", precision = 5, scale = 2)
    private BigDecimal occupancyPercentage;

    @Column(name = "session_status", length = Integer.MAX_VALUE)
    private String sessionStatus;

    @Column(name = "base_ticket_price", precision = 10, scale = 2)
    private BigDecimal baseTicketPrice;

    @Column(name = "created_at")
    private Instant createdAt;

}