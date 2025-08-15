package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.GenreService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;


    @PostMapping("/create")
    @ApiMessage("create a genre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createGenre(@RequestBody Genre genre) throws InvalidExceptions {
        genreService.createGenre(genre);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("get detail genre")
    public ResponseEntity<GenreDTO> getGenre(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok().body(genreService.getGenre(id));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete genre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) throws InvalidExceptions {
        genreService.deleteGenre(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    @ApiMessage("update genre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreDTO> updateGenre(@RequestBody GenreDTO genreDTO) throws InvalidExceptions{
        return ResponseEntity.ok(genreService.updateGenre(genreDTO));
    }
    @GetMapping()
    @ApiMessage("get and search genres")
    public ResponseEntity<ResultPagination> searchGenre(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false,defaultValue = "") String keyword,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(genreService.searchGenre(page,size,keyword,desc));
    }
}
