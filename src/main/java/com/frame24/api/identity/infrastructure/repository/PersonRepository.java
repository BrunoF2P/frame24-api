package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByCpf(String cpf);

    Optional<Person> findByPassportNumber(String passportNumber);

    boolean existsByCpf(String cpf);

    java.util.List<Person> findByFullNameContainingIgnoreCase(String name);
}
