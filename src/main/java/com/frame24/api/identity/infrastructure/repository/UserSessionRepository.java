package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.UserSession;
import com.frame24.api.identity.domain.enums.SessionContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByIdentity_Id(Long identityId);

    List<UserSession> findByIdentity_IdAndActiveTrue(Long identityId);

    Optional<UserSession> findBySessionId(String sessionId);

    Optional<UserSession> findByAccessTokenHash(String accessTokenHash);

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    List<UserSession> findBySessionContext(SessionContext sessionContext);

    @Modifying
    @Query("UPDATE UserSession s SET s.revoked = true, s.revokedAt = :revokedAt WHERE s.identity.id = :identityId AND s.revoked = false")
    void revokeAllByIdentityId(@Param("identityId") Long identityId, @Param("revokedAt") Instant revokedAt);

    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.expiresAt < :now AND s.active = true")
    void deactivateExpiredSessions(@Param("now") Instant now);
}
