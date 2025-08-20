package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NotNull
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvanceSearchRequest {
    String title;
    Set<Long> genreIds;
    Set<Long> countryIds;
    Set<Long> languageIds;

    @DecimalMin(value = "0.00", message = "Score must be between 0.00 and 100.00")
    @DecimalMax(value = "100.00", message = "Score must be between 0.00 and 100.00")
    BigDecimal minVoteAverage;
    @DecimalMin(value = "0.00", message = "Score must be between 0.00 and 100.00")
    @DecimalMax(value = "100.00", message = "Score must be between 0.00 and 100.00")
    BigDecimal maxVoteAverage;
    @Min(value = 1, message = "Min runtime must be >= 1 minute")
    @Max(value = 360, message = "Min runtime must be <= 360 minutes")
    Integer minRuntime;
    @Min(value = 1, message = "Max runtime must be >= 1 minute")
    @Max(value = 360, message = "Min runtime must be <= 360 minutes")
    Integer maxRuntime;

    LocalDate fromReleaseDate;
    LocalDate toReleaseDate;

    String sortBy = "releaseDate";
    String sortDirection = "desc";

    @Min(value = 1, message = "Page must be >= 1")
    int page = 1;
    @Min(value = 1, message = "Size must be >= 1")
    int size = 10;
}
