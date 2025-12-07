package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "role_permissions", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class RolePermission {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CustomRole role;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Permission permission;

    @Column(name = "conditions", length = Integer.MAX_VALUE)
    private String conditions;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "granted_at")
    private Instant grantedAt;

    @Column(name = "granted_by", length = Integer.MAX_VALUE)
    private String grantedBy;

}