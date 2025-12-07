package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import com.frame24.api.identity.domain.enums.IdentityType;
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
@Table(name = "identities", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class Identity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Person person;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 200)
    @Column(name = "external_id", length = 200)
    private String externalId;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "password_expires_at")
    private Instant passwordExpiresAt;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("false")
    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Size(max = 100)
    @Column(name = "email_verification_token", length = 100)
    private String emailVerificationToken;

    @Column(name = "email_verification_expires_at")
    private Instant emailVerificationExpiresAt;

    @Column(name = "blocked_until")
    private Instant blockedUntil;

    @Column(name = "block_reason", length = Integer.MAX_VALUE)
    private String blockReason;

    @ColumnDefault("0")
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "last_failed_login")
    private Instant lastFailedLogin;

    @ColumnDefault("false")
    @Column(name = "requires_2fa")
    private Boolean requires2fa;

    @Size(max = 100)
    @Column(name = "secret_2fa", length = 100)
    private String secret2fa;

    @Column(name = "backup_codes_2fa", length = Integer.MAX_VALUE)
    private String backupCodes2fa;

    @Size(max = 100)
    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expires_at")
    private Instant resetTokenExpiresAt;

    @Column(name = "last_login_date")
    private Instant lastLoginDate;

    @Size(max = 45)
    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Size(max = 500)
    @Column(name = "last_user_agent", length = 500)
    private String lastUserAgent;

    @ColumnDefault("0")
    @Column(name = "login_count")
    private Integer loginCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_identity_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Identity linkedIdentity;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "identity")
    private Set<CompanyUser> companyUsers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "linkedIdentity")
    private Set<Identity> identities = new LinkedHashSet<>();
    @OneToMany(mappedBy = "identity")
    private Set<UserSession> userSessions = new LinkedHashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'CUSTOMER'")
    @Column(name = "identity_type", nullable = false, columnDefinition = "identity_type")
    private IdentityType identityType;
}