package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import jakarta.validation.Valid;

public interface CountryService {
    void createCountry(Country country) throws InvalidExceptions;
    CountryDTO getCountry(Long id) throws InvalidExceptions;
    void deleteCountry(Long id) throws InvalidExceptions;

    ResultPagination searchCountries(int page, int size, String keyword, boolean desc);

    void updateCountry(@Valid Country country) throws InvalidExceptions;
}
