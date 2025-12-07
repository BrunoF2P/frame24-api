package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "custom_roles", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class CustomRole {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_system_role", nullable = false)
    private Boolean isSystemRole = false;

    @ColumnDefault("0")
    @Column(name = "hierarchy_level")
    private Integer hierarchyLevel;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "role")
    private Set<CompanyUser> companyUsers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

}