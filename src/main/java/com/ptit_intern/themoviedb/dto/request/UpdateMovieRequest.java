package com.ptit_intern.themoviedb.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMovieRequest {
    @NotNull(message = "Movie Id is required for update")
    Long id;
    @Size(max = 255,message = "Title must not exceed 255 characters")
    String title;
    @Size(max = 255,message = "Original title must not exceed 255 characters")
    String originalTitle;
    String overview;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

    @Min(value = 1, message = "Runtime must be positive")
    @Max(value = 1000, message = "Runtime must not exceed 1000 minutes")
    Integer runtime;

    @DecimalMin(value = "0.0", message = "Vote average must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Vote average must be at most 100.0")
    @Digits(integer = 3, fraction = 2, message = "Vote average must be a number with up to 2 decimal places")
    BigDecimal voteAverage;

    @Min(value = 0, message = "Vote count must be non-negative")
    Integer voteCount;

    @Size(max = 255, message = "trailer url must not exceed 255 characters", min = 1)
    String trailerUrl;

    @Min(value = 0, message = "Budget must be non-negative")
    Long budget;

    @Min(value = 0, message = "Revenue must be non-negative")
    Long revenue;

    @Size(max = 500, message = "Tagline must not exceed 500 characters")
    String tagline;

    @Size(max = 255, message = "homepage url must not exceed 255 characters", min = 1)
    String homepageUrl;

    @Pattern(regexp = "^(Released|In Production|Post Production|Planned|Canceled)$",
            message = "Status must be one of: Released, In Production, Post Production, Planned, Canceled")
    String status;

    // Image handling
    MultipartFile poster;
    Boolean removePoster = false;
    MultipartFile backdrop;
    Boolean removeBackdrop = false;

    // Relationship IDs
    Set<Long> genreIds;
    Set<Long> countryIds;
    Set<Long> languageIds;
    Set<Long> companyIds;
    Set<Long> personIds;
}
