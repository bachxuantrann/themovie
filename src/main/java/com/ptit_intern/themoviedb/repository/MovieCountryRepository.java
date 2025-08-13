package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.entity.MovieCountry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MovieCountryRepository extends JpaRepository<MovieCountry, Long> {
    @Query("SELECT mc.country FROM MovieCountry mc WHERE mc.movie.id = :movieId ORDER BY mc.country.name")
    List<Country> findCountriesByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT mc.country.id FROM MovieCountry mc WHERE mc.movie.id = :movieId")
    Set<Long> findExistingCountryIdsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM MovieCountry mc WHERE mc.movie.id = :movieId AND mc.country.id = :countryId")
    void deleteByMovieIdAndCountryId(@Param("movieId") Long movieId, @Param("countryId") Long countryId);

    @Modifying
    @Query("DELETE FROM MovieCountry mc WHERE mc.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(mc) FROM MovieCountry mc WHERE mc.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    boolean existsByMovieIdAndCountryId(Long movieId, Long countryId);
}
