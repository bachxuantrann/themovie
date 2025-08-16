package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.CountryService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    @PostMapping("/create")
    @ApiMessage("create new country")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createCountry(@RequestBody @Valid Country country) throws InvalidExceptions {
        countryService.createCountry(country);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("get detail country")
    public ResponseEntity<CountryDTO> getCountry(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok().body(countryService.getCountry(id));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete a country")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) throws InvalidExceptions {
        countryService.deleteCountry(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping()
    @ApiMessage("get and search countries")
    public ResponseEntity<ResultPagination> searchCountries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false,defaultValue = "") String keyword,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(countryService.searchCountries(page,size,keyword,desc));
    }
    @PutMapping()
    @ApiMessage("update info countries")
    public ResponseEntity<Void> updateCountry(@RequestBody @Valid Country country) throws InvalidExceptions {
        countryService.updateCountry(country);
        return ResponseEntity.ok().build();
    }
}
