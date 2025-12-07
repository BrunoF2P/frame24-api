package com.frame24.api.catalog.infrastructure.repository;

import com.frame24.api.catalog.domain.MoviesOnCategory;
import com.frame24.api.catalog.domain.MoviesOnCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesOnCategoryRepository extends JpaRepository<MoviesOnCategory, MoviesOnCategoryId> {
}
