package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_attributes", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class UserAttribute {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CompanyUser user;

    @Size(max = 100)
    @NotNull
    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Size(max = 500)
    @NotNull
    @Column(name = "value", nullable = false, length = 500)
    private String value;

    @Size(max = 20)
    @Column(name = "data_type", length = 20)
    private String dataType;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = Integer.MAX_VALUE)
    private String createdBy;

}