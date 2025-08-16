package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.entity.MovieLanguage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface MovieLanguageRepository extends JpaRepository<MovieLanguage, Long> {
    @Query("SELECT ml.language FROM MovieLanguage ml WHERE ml.movie.id = :movieId ORDER BY ml.language.name")
    List<Language> findLanguagesByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT ml.language.id FROM MovieLanguage ml WHERE ml.movie.id = :movieId")
    Set<Long> findExistingLanguageIdsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM MovieLanguage ml WHERE ml.movie.id = :movieId AND ml.language.id = :languageId")
    void deleteByMovieIdAndLanguageId(@Param("movieId") Long movieId, @Param("languageId") Long languageId);

    @Modifying
    @Query("DELETE FROM MovieLanguage ml WHERE ml.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(ml) FROM MovieLanguage ml WHERE ml.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    boolean existsByMovieIdAndLanguageId(Long movieId, Long languageId);
}
