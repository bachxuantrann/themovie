package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {
    @Modifying
    @Query("DELETE FROM Rating rt WHERE rt.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.movie.id = :movieId")
    Long countRatingsByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Optional<BigDecimal> findAverageRatingByMovieId(@Param("movieId") Long movieId);

    Page<Rating> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT r FROM Rating r " +
            "JOIN FETCH r.movie m " +
            "WHERE r.user.id = :userId " +
            "ORDER BY r.created_at DESC")
    Page<Rating> findByUserIdWithMovie(@Param("userId") Long userId, Pageable pageable);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);
}
