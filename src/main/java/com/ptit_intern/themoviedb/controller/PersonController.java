package com.ptit_intern.themoviedb.controller;


import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.dto.request.CreatePersonRequest;
import com.ptit_intern.themoviedb.dto.request.UpdatePersonRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.PersonService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping("/create")
    @ApiMessage("creat a person")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createPerson(@ModelAttribute @Valid CreatePersonRequest createPersonRequest) throws InvalidExceptions, IOException {
        personService.createPerson(createPersonRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("get person")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok(personService.getPerson(id));
    }
    @DeleteMapping("/{id}")
    @ApiMessage("delete person")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) throws InvalidExceptions {
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping
    @ApiMessage("updapte info person")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePerson(@Valid @ModelAttribute UpdatePersonRequest request) throws InvalidExceptions,IOException {
        personService.updatePerson(request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    @ApiMessage("get and search persons")
    public ResponseEntity<ResultPagination> searchPersons (
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String career,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(personService.searchPersons(page,size,keyword,career,desc));
    }
}
