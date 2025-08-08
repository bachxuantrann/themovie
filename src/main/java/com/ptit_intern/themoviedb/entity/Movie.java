package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

//    Relationships
    @OneToMany(mappedBy = "movie",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<MovieGenre> movieGenres = new HashSet<>();
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCast> movieCasts = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCompany> movieCompanies = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCountry> movieCountries = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieLanguage> movieLanguages = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Rating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFavoriteMovie> userFavoriteMovies = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ListItem> listItems = new HashSet<>();
}
