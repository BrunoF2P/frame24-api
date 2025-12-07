package com.frame24.api.operations.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "showtime_schedule", schema = "operations")
@EntityListeners(com.frame24.api.common.id.SnowflakeEntityListener.class)
public class ShowtimeSchedule {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "cinema_complex_id", nullable = false)
    private CinemaComplex cinemaComplex;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "audio_type")
    private AudioType audioType;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "projection_type")
    private ProjectionType projectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "session_language")
    private SessionLanguage sessionLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "status")
    private SessionStatus status;

    @ColumnDefault("0")
    @Column(name = "available_seats")
    private Integer availableSeats;

    @ColumnDefault("0")
    @Column(name = "sold_seats")
    private Integer soldSeats;

    @ColumnDefault("0")
    @Column(name = "blocked_seats")
    private Integer blockedSeats;

    @NotNull
    @Column(name = "base_ticket_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseTicketPrice;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @OneToMany(mappedBy = "showtime")
    private Set<SessionSeatStatus> sessionSeatStatuses = new LinkedHashSet<>();

}