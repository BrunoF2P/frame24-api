package com.frame24.api.identity.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import com.frame24.api.identity.domain.enums.CompanyPlanType;
import com.frame24.api.identity.domain.enums.TaxRegimeType;
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
@Table(name = "companies", schema = "identity")
@EntityListeners(SnowflakeEntityListener.class)
public class Company {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 200)
    @NotNull
    @Column(name = "corporate_name", nullable = false, length = 200)
    private String corporateName;

    @Size(max = 200)
    @Column(name = "trade_name", length = 200)
    private String tradeName;

    @Size(max = 18)
    @NotNull
    @Column(name = "cnpj", nullable = false, length = 18)
    private String cnpj;

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

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 20)
    @Column(name = "mobile", length = 20)
    private String mobile;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 200)
    @Column(name = "website", length = 200)
    private String website;

    @Size(max = 20)
    @Column(name = "state_registration", length = 20)
    private String stateRegistration;

    @Size(max = 20)
    @Column(name = "municipal_registration", length = 20)
    private String municipalRegistration;

    @ColumnDefault("false")
    @Column(name = "recine_opt_in")
    private Boolean recineOptIn;

    @Column(name = "recine_join_date")
    private LocalDate recineJoinDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "tenant_slug", nullable = false, length = 50)
    private String tenantSlug;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @ColumnDefault("1")
    @Column(name = "max_complexes")
    private Integer maxComplexes;

    @ColumnDefault("50")
    @Column(name = "max_employees")
    private Integer maxEmployees;

    @ColumnDefault("10")
    @Column(name = "max_storage_gb")
    private Integer maxStorageGb;

    @Column(name = "plan_expires_at")
    private LocalDate planExpiresAt;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("false")
    @Column(name = "suspended")
    private Boolean suspended;

    @Column(name = "suspension_reason", length = Integer.MAX_VALUE)
    private String suspensionReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "company")
    private Set<CustomRole> customRoles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "company")
    private Set<CompanyUser> companyUsers = new LinkedHashSet<>();
    @OneToMany(mappedBy = "company")
    private Set<Permission> permissions = new LinkedHashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'BASIC'")
    @Column(name = "plan_type", nullable = false)
    private CompanyPlanType planType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_regime", nullable = false)
    private TaxRegimeType taxRegime;
}