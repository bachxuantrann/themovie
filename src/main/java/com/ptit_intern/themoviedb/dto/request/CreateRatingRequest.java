package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRatingRequest {
    @NotNull(message = "userId is required")
    private Long userId;
    @NotNull(message = "movieId is required")
    private Long movieId;
    @NotNull(message = "score is required")
    @DecimalMin(value = "0.00", message = "Score must be between 0.00 and 100.00")
    @DecimalMax(value = "100.00", message = "Score must be between 0.00 and 100.00")
    private BigDecimal score;
}
