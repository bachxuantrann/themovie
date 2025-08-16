package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

import java.io.IOException;

public interface MovieService {
    MovieDTO createMovie(CreateMovieRequest request) throws IOException;

    MovieDTO getMovie(Long id) throws InvalidExceptions;

    MovieDTO updateMovie(UpdateMovieRequest request) throws InvalidExceptions, IOException;
}
