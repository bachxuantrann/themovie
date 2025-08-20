package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.AdvanceSearchRequest;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.dto.response.MovieDetailResponse;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MovieService {
    void createMovie(CreateMovieRequest request) throws IOException;

    MovieDTO getMovie(Long id) throws InvalidExceptions;

    void updateMovie(UpdateMovieRequest request) throws InvalidExceptions, IOException;

    void deleteMovie(Long id) throws InvalidExceptions;

    MovieDetailResponse getMovieDetail(Long id) throws InvalidExceptions;

    ResultPagination advancedSearch(AdvanceSearchRequest request) throws InvalidExceptions;

    List<MovieDTO> getPopularMovies();

    List<MovieDTO> getTopRatedMovies();

    ResultPagination searchByTitle(String keyword, int page, int size, boolean desc);

    Map<String,Object> searchGeneral(String keyword, int page, int size, boolean desc);
}
