package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import com.frame24.api.identity.domain.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "persons", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class Person {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 14)
    @Column(name = "cpf", length = 14)
    private String cpf;

    @Size(max = 50)
    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Size(max = 200)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 20)
    @Column(name = "mobile", length = 20)
    private String mobile;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 10)
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Size(max = 300)
    @Column(name = "street_address", length = 300)
    private String streetAddress;

    @Size(max = 20)
    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Size(max = 100)
    @Column(name = "address_complement", length = 100)
    private String addressComplement;

    @Size(max = 100)
    @Column(name = "neighborhood", length = 100)
    private String neighborhood;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 2)
    @Column(name = "state", length = 2)
    private String state;

    @Size(max = 2)
    @ColumnDefault("'BR'")
    @Column(name = "country", length = 2)
    private String country;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "person")
    private Set<Identity> identities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PREFER_NOT_TO_SAY'")
    @Column(name = "gender", columnDefinition = "gender_enum")
    private Gender gender;
}