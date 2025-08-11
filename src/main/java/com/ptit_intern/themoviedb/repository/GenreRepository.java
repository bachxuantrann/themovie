package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
