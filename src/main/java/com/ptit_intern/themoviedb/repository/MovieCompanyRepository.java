package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Company;
import com.ptit_intern.themoviedb.entity.MovieCompany;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MovieCompanyRepository extends JpaRepository<MovieCompany, Long> {
    @Query("SELECT mc.company FROM MovieCompany mc WHERE mc.movie.id = :movieId ORDER BY mc.company.name")
    List<Company> findCompaniesByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT mc.company.id FROM MovieCompany mc WHERE mc.movie.id = :movieId")
    Set<Long> findExistingCompanyIdsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM MovieCompany mc WHERE mc.movie.id = :movieId AND mc.company.id = :companyId")
    void deleteByMovieIdAndCompanyId(@Param("movieId") Long movieId, @Param("companyId") Long companyId);

    @Modifying
    @Query("DELETE FROM MovieCompany mc WHERE mc.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(mc) FROM MovieCompany mc WHERE mc.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    boolean existsByMovieIdAndCompanyId(Long movieId, Long companyId);
}
