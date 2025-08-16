package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByName(String name);
    @Query("SELECT g FROM Genre g " +
            "WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Genre> searchGenres(@Param("keyword") String keyword, Pageable pageable);
}
