package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import com.frame24.api.identity.domain.enums.SessionContext;
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
@Table(name = "user_sessions", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class UserSession {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "identity_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Identity identity;

    @Column(name = "company_id")
    private Long companyId;

    @Size(max = 255)
    @NotNull
    @Column(name = "access_token_hash", nullable = false)
    private String accessTokenHash;

    @Size(max = 255)
    @Column(name = "refresh_token_hash")
    private String refreshTokenHash;

    @Size(max = 100)
    @NotNull
    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_activity")
    private Instant lastActivity;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 500)
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Size(max = 255)
    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("false")
    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "revoked_at")
    private Instant revokedAt;
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "session_context", nullable = false, columnDefinition = "session_context")
    private SessionContext sessionContext;
}