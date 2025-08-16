package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends BaseEntity<MovieDTO> {
    @Column(name = "title", nullable = false)
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
    @Column(name = "poster_public_id")
    String posterPublicId;
    @Column(name = "backdrop_public_id")
    String backdropPublicId;
    @Column(name = "backdrop_path")
    String backdropPath;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 5, scale = 2, name = "vote_average")
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
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
