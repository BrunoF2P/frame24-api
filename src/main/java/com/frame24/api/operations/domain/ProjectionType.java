package com.frame24.api.operations.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "projection_types", schema = "operations")
@EntityListeners(SnowflakeEntityListener.class)
public class ProjectionType {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ColumnDefault("0.00")
    @Column(name = "additional_value", precision = 10, scale = 2)
    private BigDecimal additionalValue;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "projectionType")
    private Set<Room> rooms = new LinkedHashSet<>();

    @OneToMany(mappedBy = "projectionType")
    private Set<ShowtimeSchedule> showtimeSchedules = new LinkedHashSet<>();

}