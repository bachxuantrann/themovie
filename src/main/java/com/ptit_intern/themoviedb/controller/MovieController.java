package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.MovieService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/create")
    @ApiMessage("create new movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> createMovie(
            @Valid @ModelAttribute CreateMovieRequest request
    ) throws IOException {
        log.info("Create new movie {}", request.getTitle());
        return ResponseEntity.ok().body(movieService.createMovie(request));
    }

    @GetMapping("/{id}")
    @ApiMessage("get info detail movie")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable Long id) throws InvalidExceptions {
        return ResponseEntity.ok(movieService.getMovie(id));
    }

    @PutMapping()
    @ApiMessage("update movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> updateMovie(
            @Valid @ModelAttribute UpdateMovieRequest request
    ) throws InvalidExceptions, IOException {

        return ResponseEntity.ok().body(movieService.updateMovie(request));
    }
    @DeleteMapping("/{id}")
    @ApiMessage("delete movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) throws InvalidExceptions {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

}
