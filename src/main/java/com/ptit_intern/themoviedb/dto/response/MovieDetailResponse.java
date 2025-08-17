package com.ptit_intern.themoviedb.dto.response;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailResponse {
    MovieDTO movie;
    List<MovieCastInfo> casting;
    List<MovieCastInfo> crew;
}
