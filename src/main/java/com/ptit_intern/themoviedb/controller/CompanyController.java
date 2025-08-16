package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import com.ptit_intern.themoviedb.dto.request.CreateCompanyRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateCompanyRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.CompanyService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/create")
    @ApiMessage("create company")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createCompany(@Valid @ModelAttribute CreateCompanyRequest request) throws InvalidExceptions, IOException {
        companyService.createCompany(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("get info detail company")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok(companyService.getCompany(id));
    }
    @DeleteMapping("/{id}")
    @ApiMessage("delete a company with id")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) throws InvalidExceptions {
        companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping()
    @ApiMessage("get and search company")
    public ResponseEntity<ResultPagination> searchCompanies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false,defaultValue = "") String keyword,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(companyService.searchCompanies(page,size,keyword,desc));
    }
    @PutMapping()
    @ApiMessage("update info company")
    public ResponseEntity<Void> updateCompany(@Valid @ModelAttribute UpdateCompanyRequest request) throws InvalidExceptions, IOException {
        companyService.updateCompany(request);
        return ResponseEntity.ok().build();
    }
}
