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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EntityListeners(SnowflakeEntityListener.class)
@Table(name = "movies", schema = "catalog")
public class Movie {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "distributor_id", nullable = false)
    private Long distributorId;

    @Size(max = 300)
    @NotNull
    @Column(name = "original_title", nullable = false, length = 300)
    private String originalTitle;

    @Size(max = 300)
    @Column(name = "brazil_title", length = 300)
    private String brazilTitle;

    @Size(max = 50)
    @Column(name = "ancine_number", length = 50)
    private String ancineNumber;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Size(max = 50)
    @Column(name = "country_of_origin", length = 50)
    private String countryOfOrigin;

    @Column(name = "production_year")
    private Integer productionYear;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"national\"", nullable = false)
    private Boolean national = false;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "synopsis", length = Integer.MAX_VALUE)
    private String synopsis;

    @Size(max = 500)
    @Column(name = "short_synopsis", length = 500)
    private String shortSynopsis;

    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;

    @Size(max = 200)
    @Column(name = "website", length = 200)
    private String website;

    @Size(max = 50)
    @Column(name = "tmdb_id", length = 50)
    private String tmdbId;

    @Size(max = 20)
    @Column(name = "imdb_id", length = 20)
    private String imdbId;

    @Column(name = "tags_json", length = Integer.MAX_VALUE)
    private String tagsJson;

    @Column(name = "worldwide_release_date")
    private LocalDate worldwideReleaseDate;

    @Size(max = 50)
    @Column(name = "original_language", length = 50)
    private String originalLanguage;

    @Size(max = 200)
    @Column(name = "slug", length = 200)
    private String slug;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "age_rating_id")
    private AgeRating ageRating;

    @Column(name = "\"movie_categoriesId\"", length = Integer.MAX_VALUE)
    private String movieCategoriesid;

    @OneToMany(mappedBy = "movie")
    private Set<MovieCast> movieCasts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "movie")
    private Set<MovieMedia> movieMedia = new LinkedHashSet<>();

    @OneToMany(mappedBy = "movie")
    private Set<MoviesOnCategory> moviesOnCategories = new LinkedHashSet<>();

}