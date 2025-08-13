package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language,Long> {
    boolean existsByLanguageCodeAndName(String languageCode, String name);
    boolean existsByLanguageCodeAndNameAndIdNot(String languageCode, String name, Long id);

}
