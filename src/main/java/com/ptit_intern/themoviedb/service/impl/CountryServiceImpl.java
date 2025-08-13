package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.CountryRepository;
import com.ptit_intern.themoviedb.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public void createCountry(Country country) throws InvalidExceptions {
        if (isExists(country.getCountryCode(),country.getName())){
            throw new InvalidExceptions("Country is existed");
        }
        Country newCountry = new Country();
        newCountry.setCountryCode(country.getCountryCode());
        newCountry.setName(country.getName());
        countryRepository.save(newCountry);
    }
    @Override
    public CountryDTO getCountry(Long id) throws InvalidExceptions {
        return countryRepository.findById(id).orElseThrow(
                () -> new InvalidExceptions("Language not found")
        ).toDTO(CountryDTO.class);
    }

    @Override
    public void deleteCountry(Long id) throws InvalidExceptions {
        if (this.countryRepository.findById(id).isPresent()) {
            countryRepository.deleteById(id);
        }
        else {
            throw new InvalidExceptions("Country not found");
        }
    }
    private boolean isExists(String countryCode, String name){
        return countryRepository.existsByCountryCodeAndName(countryCode, name);
    }
}
