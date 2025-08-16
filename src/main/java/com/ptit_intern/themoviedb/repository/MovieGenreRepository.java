package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.entity.MovieGenre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    @Query("SELECT mg.genre FROM MovieGenre mg WHERE mg.movie.id = :movieId ORDER BY mg.genre.name")
    List<Genre> findGenresByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT mg.genre.id FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    Set<Long> findExistingGenreIdsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.id = :movieId AND mg.genre.id = :genreId")
    void deleteByMovieIdAndGenreId(@Param("movieId") Long movieId, @Param("genreId") Long genreId);

    @Modifying
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(mg) FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    boolean existsByMovieIdAndGenreId(Long movieId, Long genreId);

}
