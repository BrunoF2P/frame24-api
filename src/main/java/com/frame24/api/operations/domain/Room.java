package com.frame24.api.operations.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "rooms", schema = "operations")
@EntityListeners(com.frame24.api.common.id.SnowflakeEntityListener.class)
public class Room {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cinema_complex_id", nullable = false)
    private CinemaComplex cinemaComplex;

    @Size(max = 10)
    @NotNull
    @Column(name = "room_number", nullable = false, length = 10)
    private String roomNumber;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @NotNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "projection_type")
    private ProjectionType projectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "audio_type")
    private AudioType audioType;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "seat_layout", length = Integer.MAX_VALUE)
    private String seatLayout;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "total_columns")
    private Integer totalColumns;

    @Size(max = 30)
    @Column(name = "room_design", length = 30)
    private String roomDesign;

    @Size(max = 255)
    @Column(name = "layout_image")
    private String layoutImage;

    @OneToMany(mappedBy = "room")
    private Set<Seat> seats = new LinkedHashSet<>();

    @OneToMany(mappedBy = "room")
    private Set<ShowtimeSchedule> showtimeSchedules = new LinkedHashSet<>();

}