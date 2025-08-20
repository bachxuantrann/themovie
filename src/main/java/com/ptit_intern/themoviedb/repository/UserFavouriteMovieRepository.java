package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.UserFavoriteMovie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavouriteMovieRepository extends JpaRepository<UserFavoriteMovie, Long> {
    @Modifying
    @Query("DELETE FROM UserFavoriteMovie ufm WHERE ufm.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT ufm.movie.id " +
            "FROM UserFavoriteMovie ufm " +
            "WHERE ufm.user.id = :userId")
    Page<Long> findFavoriteMovieIdsByUserId(Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserFavoriteMovie ufm WHERE ufm.user.id = :userId AND ufm.movie.id = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
