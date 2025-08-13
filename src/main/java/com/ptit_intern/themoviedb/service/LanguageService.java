package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

public interface LanguageService {
    void createLanguage(Language language) throws InvalidExceptions;
    LanguageDTO getLanguage(Long id) throws InvalidExceptions;
    void deleteLanguage(Long id) throws InvalidExceptions;
}
