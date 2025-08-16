package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.entity.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByCountryCodeAndName(String countryCode, String name);
    @Query("SELECT c FROM Country c " +
            "WHERE LOWER(c.countryCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Country> searchCountries(@Param("keyword") String keyword, Pageable pageable);
    boolean existsByCountryCodeAndIdNot(String countryCode, long id);
    boolean existsByNameAndIdNot(String name, long id);
}
