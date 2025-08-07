package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends BaseEntity {
    String title;
    @Column(name = "original_title")
    String originalTitle;
    @Column(columnDefinition = "TEXT")
    String overview;
    @Column(name = "release_date")
    LocalDate releaseDate;
    Integer runtime;
    @Column(name = "poster_path")
    String posterPath;
    @Column(name = "backdrop_path")
    String backdropPath;
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Column(precision = 4, scale = 1,name = "vote_average")
    BigDecimal voteAverage;
    @Column(name = "vote_count")
    Integer voteCount;
    @Column(name = "trailer_url")
    String trailerUrl;
    Long budget;
    Long revenue;
    @Column(name = "tag_line")
    String tagline;
    @Column(name = "homepage_url")
    String homepageUrl;
    String status;
}
