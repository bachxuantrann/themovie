package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
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
    @PutMapping()
    @ApiMessage("update language")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateLanguage(@RequestBody @Valid LanguageDTO languageDTO) throws InvalidExceptions {
        languageService.updateLanguage(languageDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping()
    @ApiMessage("get and search language")
    public ResponseEntity<ResultPagination> searchLanguages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false,defaultValue = "") String keyword,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(languageService.searchLanguages(page,size,keyword,desc));
    }
}
