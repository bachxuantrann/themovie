package com.ptit_intern.themoviedb.dto.dtoClass;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO extends BaseDTO {
    @NotNull(message = "id of rating is required")
    private Long id;
    @NotNull(message = "score is required")
    private BigDecimal score;
    @NotNull(message = "userId is required")
    private Long userId;
    @NotNull(message = "movieId is required")
    private Long movieId;
}
