package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.entity.Movie;

import java.io.IOException;

public interface MovieService {
    Movie createMovie(CreateMovieRequest request) throws IOException;
}
