package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m JOIN MovieGenre mg ON m.id = mg.movie.id WHERE mg.genre.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN MovieCountry mc ON m.id = mc.movie.id WHERE mc.country.id = :countryId")
    Page<Movie> findByCountryId(@Param("countryId") Long countryId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN MovieLanguage ml ON m.id = ml.movie.id WHERE ml.language.id = :languageId")
    Page<Movie> findByLanguageId(@Param("languageId") Long languageId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN MovieCompany mc ON m.id = mc.movie.id WHERE mc.company.id = :companyId")
    Page<Movie> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN MovieCast mc ON m.id = mc.movie.id WHERE mc.person.id = :personId")
    Page<Movie> findByPersonId(@Param("personId") Long personId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN MovieCast mc ON m.id = mc.movie.id WHERE mc.person.id = :personId AND mc.job = :job")
    Page<Movie> findByPersonIdAndJob(@Param("personId") Long personId, @Param("job") String job, Pageable pageable);

    Optional<Movie> findByTitleAndReleaseDate(String title, LocalDate releaseDate);
    boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);

    @Query("SELECT m FROM Movie m " +
            "WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.overview) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> searchMovies(@Param("keyword") String keyword, Pageable pageable);
    // Lấy 10 phim mới nhất
    List<Movie> findTop10ByOrderByReleaseDateDesc();
    List<Movie> findTop10ByOrderByVoteAverageDesc();
}
