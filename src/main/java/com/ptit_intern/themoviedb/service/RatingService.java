package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.RatingDTO;
import com.ptit_intern.themoviedb.dto.request.CreateRatingRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import jakarta.validation.Valid;

import java.util.Map;

public interface RatingService {
    void createOrUpdateRating(@Valid CreateRatingRequest request) throws InvalidExceptions;

    RatingDTO getUserRatingForMovie(Long movieId) throws InvalidExceptions;

    ResultPagination getUserRatings(Long id, int page, int size, boolean desc) throws InvalidExceptions;

    void deleteRating(Long userId,Long movieId) throws InvalidExceptions;
}
