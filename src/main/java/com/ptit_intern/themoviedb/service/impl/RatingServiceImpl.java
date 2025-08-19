package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.RatingDTO;
import com.ptit_intern.themoviedb.dto.request.CreateRatingRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.entity.Rating;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.MovieRepository;
import com.ptit_intern.themoviedb.repository.RatingRepository;
import com.ptit_intern.themoviedb.repository.UserRepository;
import com.ptit_intern.themoviedb.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public record MovieRatingofUser(Long id, String title, BigDecimal yourRating, BigDecimal voteAverage,
                                    Integer voteCount, String posterPath, String backdropPath, LocalDate releaseDate) {
    }

    @Override
    public void createOrUpdateRating(CreateRatingRequest request) throws InvalidExceptions {
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new InvalidExceptions("User not found with id: " + request.getUserId())
        );
        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(
                () -> new InvalidExceptions("Movie not found with id: " + request.getMovieId())
        );
        Rating rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId()).orElse(new Rating());
        rating.setScore(request.getScore());
        rating.setUser(user);
        rating.setMovie(movie);
        ratingRepository.save(rating);
        updateMovieRatingStats(movie.getId());
    }

    @Override
    public RatingDTO getUserRatingForMovie(Long movieId) throws InvalidExceptions {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(userName);
        Long userId = user.getId();
        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId).orElseThrow(
                () -> new InvalidExceptions("movie or user is not existed")
        );
        return rating.toDTO(RatingDTO.class);
    }

    @Override
    public ResultPagination getUserRatings(Long id, int page, int size, boolean desc) throws InvalidExceptions {
        if (!userRepository.existsById(id)) {
            throw new InvalidExceptions("User not found");
        }
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Rating> ratings = ratingRepository.findByUserIdWithMovie(id, pageable);
        Page<MovieRatingofUser> movieRatings = ratings.map(
                rating -> new MovieRatingofUser(
                        rating.getMovie().getId(),
                        rating.getMovie().getTitle(),
                        rating.getScore(),
                        rating.getMovie().getVoteAverage(),
                        rating.getMovie().getVoteCount(),
                        rating.getMovie().getPosterPath(),
                        rating.getMovie().getBackdropPath(),
                        rating.getMovie().getReleaseDate()
                )
        );
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(movieRatings.getContent());
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(movieRatings.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(movieRatings.getTotalPages());
        return resultPagination;
    }

    @Override
    public void deleteRating(Long userId, Long movieId) throws InvalidExceptions {
        if (!ratingRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new InvalidExceptions("User or movie not found");
        }
        ratingRepository.deleteByUserIdAndMovieId(userId, movieId);
        updateMovieRatingStats(movieId);
    }

    //    Helper methods
    private void updateMovieRatingStats(Long movieId) throws InvalidExceptions {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new InvalidExceptions("Movie not found with id: " + movieId)
        );
        BigDecimal avgRating = ratingRepository.findAverageRatingByMovieId(movieId).orElse(new BigDecimal(0));
        Long ratingcount = ratingRepository.countRatingsByMovieId(movieId);
        movie.setVoteAverage(avgRating);
        movie.setVoteCount(ratingcount.intValue());
        movieRepository.save(movie);
    }
}
