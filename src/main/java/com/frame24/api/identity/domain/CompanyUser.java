package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
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
@Table(name = "company_users", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class CompanyUser {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "identity_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Identity identity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Column(name = "employee_id")
    private Long employeeId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CustomRole role;

    @Size(max = 100)
    @Column(name = "department", length = 100)
    private String department;

    @Size(max = 50)
    @Column(name = "job_level", length = 50)
    private String jobLevel;

    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "allowed_complexes", length = Integer.MAX_VALUE)
    private String allowedComplexes;

    @Column(name = "ip_whitelist", length = Integer.MAX_VALUE)
    private String ipWhitelist;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "assigned_by", length = Integer.MAX_VALUE)
    private String assignedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "last_access")
    private Instant lastAccess;

    @ColumnDefault("0")
    @Column(name = "access_count")
    private Integer accessCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<UserAttribute> userAttributes = new LinkedHashSet<>();

}