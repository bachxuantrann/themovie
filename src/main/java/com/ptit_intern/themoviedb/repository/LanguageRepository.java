package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Long> {
    boolean existsByLanguageCodeAndName(String languageCode, String name);
    boolean existsByLanguageCodeAndNameAndIdNot(String languageCode, String name, Long id);
    boolean existsByLanguageCodeAndIdNot(String languageCode,Long id);
    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT l FROM Language l " +
            "WHERE LOWER(l.languageCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Language> searchLanguages(@Param("keyword") String keyword, Pageable pageable);

}
