package com.frame24.api.catalog.infrastructure.repository;

import com.frame24.api.catalog.domain.ComboProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComboProductRepository extends JpaRepository<ComboProduct, Long> {
}
