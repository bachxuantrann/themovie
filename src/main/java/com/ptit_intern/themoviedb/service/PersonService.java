package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.dto.request.CreatePersonRequest;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

import java.io.IOException;

public interface PersonService {
    void createPerson(CreatePersonRequest request) throws InvalidExceptions, IOException;

    PersonDTO getPerson(Long id) throws InvalidExceptions;

    void deletePerson(Long id) throws InvalidExceptions;
}
