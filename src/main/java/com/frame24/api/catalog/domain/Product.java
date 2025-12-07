package com.frame24.api.catalog.domain;

import com.frame24.api.common.id.SnowflakeEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EntityListeners(SnowflakeEntityListener.class)
@Table(name = "products", schema = "catalog")
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Size(max = 50)
    @NotNull
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Size(max = 200)
    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 10)
    @Column(name = "ncm_code", length = 10)
    private String ncmCode;

    @Size(max = 10)
    @ColumnDefault("'UN'")
    @Column(name = "unit", length = 10)
    private String unit;

    @ColumnDefault("0")
    @Column(name = "minimum_stock")
    private Integer minimumStock;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 50)
    @Column(name = "barcode", length = 50)
    private String barcode;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "supplier_id")
    private Long supplierId;

    @ColumnDefault("false")
    @Column(name = "is_available_online")
    private Boolean isAvailableOnline;

    @OneToMany(mappedBy = "product")
    private Set<ComboProduct> comboProducts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductPrice> productPrices = new LinkedHashSet<>();

}