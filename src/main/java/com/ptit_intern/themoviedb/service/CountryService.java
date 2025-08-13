package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

public interface CountryService {
    void createCountry(Country country) throws InvalidExceptions;
    CountryDTO getCountry(Long id) throws InvalidExceptions;
    void deleteCountry(Long id) throws InvalidExceptions;
}
