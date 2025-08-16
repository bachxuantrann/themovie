package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.RatingDTO;
import com.ptit_intern.themoviedb.dto.request.CreateRatingRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.RatingService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    @ApiMessage("create or update rating")
    public ResponseEntity<Void> createOrUpdateRating (@RequestBody @Valid CreateRatingRequest request) throws InvalidExceptions {
        ratingService.createOrUpdateRating(request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{movieId}")
    @ApiMessage("get user rating for movie")
    public ResponseEntity<RatingDTO> getUserRatingForMovie (@PathVariable Long movieId) throws InvalidExceptions {
        return ResponseEntity.ok(ratingService.getUserRatingForMovie(movieId));
    }
    @GetMapping("/users/{id}")
    @ApiMessage("get list ratings of user")
    public ResponseEntity<ResultPagination> getUserRatings (@PathVariable("id") Long userId,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "true") boolean desc
     ) throws InvalidExceptions {
        return ResponseEntity.ok(ratingService.getUserRatings(userId,page,size,desc));
    }
    @DeleteMapping("/{userId}/{movieId}")
    @ApiMessage("delete rating")
    public ResponseEntity<Void> deleteRating (@PathVariable("userId") Long userId,@PathVariable("movieId") Long movieId) throws InvalidExceptions {
        ratingService.deleteRating(userId,movieId);
        return ResponseEntity.ok().build();
    }
}
