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

@Getter
@Setter
@Entity
@Table(name = "session_seat_status", schema = "operations")
@EntityListeners(com.frame24.api.common.id.SnowflakeEntityListener.class)
public class SessionSeatStatus {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "showtime_id", nullable = false)
    private ShowtimeSchedule showtime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "sale_id")
    private Long saleId;

    @Size(max = 100)
    @Column(name = "reservation_uuid", length = 100)
    private String reservationUuid;

    @Column(name = "reservation_date")
    private Instant reservationDate;

    @Column(name = "expiration_date")
    private Instant expirationDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    /*
     * TODO [Reverse Engineering] create field to map the 'status' column
     * Available actions: Define target Java type | Uncomment as is | Remove column
     * mapping
     *
     * @ColumnDefault("'AVAILABLE'")
     *
     * @Column(name = "status", columnDefinition = "seat_status_enum not null")
     * private Object status;
     */
}