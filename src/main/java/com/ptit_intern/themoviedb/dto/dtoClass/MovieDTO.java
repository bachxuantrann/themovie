package com.ptit_intern.themoviedb.dto.dtoClass;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {
    Long id;
    String title;
    String originalTitle;
    String overview;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    Integer runtime;
    String posterPath;
    String posterPublicId;
    String backdropPath;
    String backdropPublicId;
    BigDecimal voteAverage;
    Integer voteCount;
    String trailerUrl;
    Long budget;
    Long revenue;
    String tagline;
    String homepageUrl;
    String status;

    // Audit fields
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant updatedAt;

    String createdBy;
    String updatedBy;

    // Related data
    Set<GenreDTO> genres;
    Set<CountryDTO> countries;
    Set<LanguageDTO> languages;
    Set<CompanyDTO> companies;
    Set<PersonDTO> persons;
    Set<CommentDTO> comments;
}
