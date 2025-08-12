package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.entity.Country;
import com.ptit_intern.themoviedb.service.CountryService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    @PostMapping("/create")
    @ApiMessage("create new country")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createCountry(@RequestBody @Valid Country country) {
        countryService.createCountry(country);
        return ResponseEntity.ok().build();
    }
}
