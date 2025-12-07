package com.frame24.api.operations.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cinema_complexes", schema = "operations")
@EntityListeners(com.frame24.api.common.id.SnowflakeEntityListener.class)
public class CinemaComplex {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Size(max = 200)
    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Size(max = 18)
    @Column(name = "cnpj", length = 18)
    private String cnpj;

    @Column(name = "address", length = Integer.MAX_VALUE)
    private String address;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 2)
    @Column(name = "state", length = 2)
    private String state;

    @Size(max = 10)
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Size(max = 7)
    @NotNull
    @Column(name = "ibge_municipality_code", nullable = false, length = 7)
    private String ibgeMunicipalityCode;

    @Size(max = 50)
    @Column(name = "ancine_registry", length = 50)
    private String ancineRegistry;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "cinemaComplex")
    private Set<Room> rooms = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cinemaComplex")
    private Set<ShowtimeSchedule> showtimeSchedules = new LinkedHashSet<>();

}