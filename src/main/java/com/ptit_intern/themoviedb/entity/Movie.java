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
    String originalTitle;
    @Column(columnDefinition = "TEXT")
    String overview;
    LocalDate releaseDate;
    Integer runtime;
    String posterPath;
    String backdropPath;
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Column(precision = 4, scale = 1)
    BigDecimal voteAverage;
    Integer voteCount;
    String trailerUrl;
    Long budget;
    Long revenue;
    String tagline;
    String homepageUrl;
    String status;
}
