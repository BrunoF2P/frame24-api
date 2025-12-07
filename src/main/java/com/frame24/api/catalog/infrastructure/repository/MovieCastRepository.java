package com.frame24.api.catalog.infrastructure.repository;

import com.frame24.api.catalog.domain.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
}
