package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
