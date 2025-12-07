package com.frame24.api.catalog.infrastructure.repository;

import com.frame24.api.catalog.domain.MovieMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieMediaRepository extends JpaRepository<MovieMedia, Long> {
}
