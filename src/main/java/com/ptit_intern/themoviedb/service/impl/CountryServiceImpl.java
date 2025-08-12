package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.repository.CountryRepository;
import com.ptit_intern.themoviedb.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public void createCountry(Country country) {
        Country newCountry = new Country();
        newCountry.setCountryCode(country.getCountryCode());
        newCountry.setName(country.getName());
        countryRepository.save(newCountry);
    }
}
