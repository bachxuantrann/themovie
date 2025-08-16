package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {
    @Modifying
    @Query("DELETE FROM Rating rt WHERE rt.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);
}
