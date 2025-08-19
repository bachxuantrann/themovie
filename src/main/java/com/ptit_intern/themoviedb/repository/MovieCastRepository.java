package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.entity.MovieCast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
    @Query("SELECT mc FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job = 'actor' ORDER BY mc.orderIndex ASC, mc.person.name ASC")
    List<MovieCast> findCastByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT mc FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job != 'actor' ORDER BY mc.job ASC, mc.person.name ASC")
    List<MovieCast> findCrewByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT COUNT(mc) FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job = :job")
    Long countByMovieIdAndJob(@Param("movieId") Long movieId, @Param("job") String job);

    @Query("SELECT COUNT(mc) FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job != :job")
    Long countByMovieIdAndJobNot(@Param("movieId") Long movieId, @Param("job") String job);

    @Modifying
    @Query("DELETE FROM MovieCast mc WHERE mc.id = :castId AND mc.movie.id = :movieId")
    void deleteByIdAndMovieId(@Param("castId") Long castId, @Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job = :job")
    void deleteByMovieIdAndJob(@Param("movieId") Long movieId, @Param("job") String job);

    @Modifying
    @Query("DELETE FROM MovieCast mc WHERE mc.movie.id = :movieId AND mc.job != :job")
    void deleteByMovieIdAndJobNot(@Param("movieId") Long movieId, @Param("job") String job);

    boolean existsByMovieIdAndPersonIdAndJob(Long movieId, Long personId, String job);

    @Modifying
    @Query("DELETE FROM MovieCast mca WHERE mca.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT mc.movie FROM MovieCast mc WHERE mc.person.id = :personId")
    List<Movie> findAllByPersonId(@Param("personId") Long personId);
}
