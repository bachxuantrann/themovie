package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.LanguageService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguageService languageService;

    @PostMapping("/create")
    @ApiMessage("create new language")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createLanguage(@RequestBody @Valid Language language) throws InvalidExceptions {
        languageService.createLanguage(language);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    @ApiMessage("get detail language")
    public ResponseEntity<LanguageDTO> getLanguage(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok().body(languageService.getLanguage(id));
    }
    @DeleteMapping("/{id}")
    @ApiMessage("delete language")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) throws InvalidExceptions {
        languageService.deleteLanguage(id);
        return ResponseEntity.ok().build();
    }
}
