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
@Table(name = "permissions", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class Permission {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Size(max = 100)
    @NotNull
    @Column(name = "resource", nullable = false, length = 100)
    private String resource;

    @Size(max = 50)
    @NotNull
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Size(max = 150)
    @NotNull
    @Column(name = "code", nullable = false, length = 150)
    private String code;

    @Size(max = 200)
    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 50)
    @Column(name = "module", length = 50)
    private String module;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "permission")
    private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

}