package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.Identity;
import com.frame24.api.identity.domain.enums.IdentityType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {

    Optional<Identity> findByEmail(@NonNull String email);

    Optional<Identity> findByExternalId(@NonNull String externalId);

    boolean existsByEmail(@NonNull String email);

    Optional<Identity> findByResetToken(@NonNull String resetToken);

    Optional<Identity> findByEmailVerificationToken(@NonNull String token);

    List<Identity> findByIdentityType(@NonNull IdentityType identityType);
}
