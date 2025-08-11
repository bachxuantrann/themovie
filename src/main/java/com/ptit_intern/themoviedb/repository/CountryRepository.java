package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
