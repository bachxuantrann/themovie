package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.entity.Movie;

import java.io.IOException;

public interface MovieService {
    MovieDTO createMovie(CreateMovieRequest request) throws IOException;
}
