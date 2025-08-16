package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFavouriteMovieRequest {
    @NotNull(message = "userId is required")
    private Long userId;
    @NotNull(message = "movieId is required")
    private Long movieId;
}
