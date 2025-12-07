package com.frame24.api.operations.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "courtesy_parameters", schema = "operations")
@EntityListeners(SnowflakeEntityListener.class)
public class CourtesyParameter {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "cinema_complex_id", nullable = false)
    private Long cinemaComplexId;

    @ColumnDefault("0.00")
    @Column(name = "courtesy_taxation_percentage", precision = 5, scale = 2)
    private BigDecimal courtesyTaxationPercentage;

    @ColumnDefault("1000")
    @Column(name = "monthly_courtesy_limit")
    private Integer monthlyCourtesyLimit;

    @NotNull
    @Column(name = "validity_start", nullable = false)
    private LocalDate validityStart;

    @Column(name = "validity_end")
    private LocalDate validityEnd;

}