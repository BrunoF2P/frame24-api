package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, Long> {

    List<UserAttribute> findByUserId(Long userId);

    Optional<UserAttribute> findByUserIdAndKey(Long userId, String key);

    void deleteByUserIdAndKey(Long userId, String key);
}
