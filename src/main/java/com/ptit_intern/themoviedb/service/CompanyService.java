package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import com.ptit_intern.themoviedb.dto.request.CreateCompanyRequest;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

import java.io.IOException;

public interface CompanyService {
    void createCompany(CreateCompanyRequest request) throws InvalidExceptions, IOException;

    CompanyDTO getCompany(Long id) throws InvalidExceptions;

    void deleteCompany(Long id) throws InvalidExceptions;
}
