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
public class CreateMovieRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255,message = "Title must not exceed 255 characters")
    String title;
    @Size(max = 255,message = "Original title must not exceed 255 characters")
    String originalTitle;
    String overview;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @Min(value = 1,message = "Runtime must be positive")
    @Max(value = 1000,message = "Runtime must not exceed at 1000 minutes")
    Integer runtime;
    @DecimalMin(value = "0.0", message = "Vote average must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Vote average must be at most 100.0")
    @Digits(integer = 3, fraction = 2, message = "Vote average must be a number with up to 2 decimal places")
    BigDecimal voteAverage;

    @Min(value = 0, message = "Vote count must be non-negative")
    Integer voteCount;

    @Pattern(regexp = "^(https?://).*", message = "Trailer URL must be a valid HTTP/HTTPS URL")
    String trailerUrl;

    @Min(value = 0, message = "Budget must be non-negative")
    Long budget;

    @Min(value = 0, message = "Revenue must be non-negative")
    Long revenue;

    @Size(max = 500, message = "Tagline must not exceed 500 characters")
    String tagline;

    @Pattern(regexp = "^(https?://).*", message = "Homepage URL must be a valid HTTP/HTTPS URL")
    String homepageUrl;

    @Pattern(regexp = "^(Released|In Production|Post Production|Planned|Canceled)$",
            message = "Status must be one of: Released, In Production, Post Production, Planned, Canceled")
    String status;

    // Image files
    MultipartFile poster;
    MultipartFile backdrop;

    // Relationship IDs
    Set<Long> genreIds;
    Set<Long> countryIds;
    Set<Long> languageIds;
    Set<Long> companyIds;
    Set<Long> personIds;
}
