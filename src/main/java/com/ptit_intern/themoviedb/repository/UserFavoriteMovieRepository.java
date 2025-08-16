package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.UserFavoriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoriteMovieRepository extends JpaRepository<UserFavoriteMovie, Long> {
    @Modifying
    @Query("DELETE FROM UserFavoriteMovie ufm  WHERE ufm.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);
}
