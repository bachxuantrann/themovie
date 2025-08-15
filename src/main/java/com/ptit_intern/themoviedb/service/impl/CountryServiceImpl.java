package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.CountryRepository;
import com.ptit_intern.themoviedb.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ResultPagination searchCountries(int page, int size, String keyword, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Country> countries = countryRepository.searchCountries(keyword, pageable);
        List<CountryDTO> countryDTOS = new ArrayList<>();
        countryDTOS = countries.stream().map(genre -> genre.toDTO(CountryDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(countryDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(countries.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(countries.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    @Override
    public void updateCountry(Country request) throws InvalidExceptions {
        Country country = countryRepository.findById(request.getId()).orElseThrow(
                () -> new InvalidExceptions("Country is not existed")
        );
        if (!countryRepository.existsByCountryCodeAndIdNot(request.getCountryCode(), request.getId())) {
            country.setCountryCode(request.getCountryCode());
        }
        else {
            throw new InvalidExceptions("Country code already exists");
        }
        if (!countryRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
            country.setName(request.getName());
        } else {
            throw new InvalidExceptions("Country name already exists");
        }
        countryRepository.save(country);
    }

    private boolean isExists(String countryCode, String name){
        return countryRepository.existsByCountryCodeAndName(countryCode, name);
    }
}
