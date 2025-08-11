package com.ptit_intern.themoviedb.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class RelationshipRepositoryFactory {
    private final MovieGenreRepository movieGenreRepository;
    private final MovieCountryRepository movieCountryRepository;
    private final MovieLanguageRepository movieLanguageRepository;
    private final MovieCompanyRepository movieCompanyRepository;
    private final MovieCastRepository movieCastRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;
    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;
}
