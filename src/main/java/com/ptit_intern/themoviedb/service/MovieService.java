package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.dto.response.MovieDetailResponse;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

import java.io.IOException;

public interface MovieService {
    void createMovie(CreateMovieRequest request) throws IOException;

    MovieDTO getMovie(Long id) throws InvalidExceptions;

    void updateMovie(UpdateMovieRequest request) throws InvalidExceptions, IOException;

    void deleteMovie(Long id) throws InvalidExceptions;

    MovieDetailResponse getMovieDetail(Long id) throws InvalidExceptions;
}
