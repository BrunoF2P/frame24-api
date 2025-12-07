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
@Table(name = "seats", schema = "operations")
@EntityListeners(com.frame24.api.common.id.SnowflakeEntityListener.class)
public class Seat {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "seat_type")
    private SeatType seatType;

    @Size(max = 10)
    @NotNull
    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode;

    @Size(max = 5)
    @NotNull
    @Column(name = "row_code", nullable = false, length = 5)
    private String rowCode;

    @NotNull
    @Column(name = "column_number", nullable = false)
    private Integer columnNumber;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @ColumnDefault("false")
    @Column(name = "accessible")
    private Boolean accessible;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "seat")
    private Set<SessionSeatStatus> sessionSeatStatuses = new LinkedHashSet<>();

}