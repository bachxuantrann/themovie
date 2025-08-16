package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import com.ptit_intern.themoviedb.dto.request.CreateCompanyRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateCompanyRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import jakarta.validation.Valid;

import java.io.IOException;

public interface CompanyService {
    void createCompany(CreateCompanyRequest request) throws InvalidExceptions, IOException;

    CompanyDTO getCompany(Long id) throws InvalidExceptions;

    void deleteCompany(Long id) throws InvalidExceptions;

    ResultPagination searchCompanies(int page, int size, String keyword, boolean desc);

    void updateCompany(@Valid UpdateCompanyRequest request) throws InvalidExceptions, IOException;
}
