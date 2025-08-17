package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.dto.response.MovieDetailResponse;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/create")
    @ApiMessage("create new movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createMovie(
            @Valid @ModelAttribute CreateMovieRequest request
    ) throws IOException {
        log.info("Create new movie {}", request.getTitle());
        movieService.createMovie(request);
        return ResponseEntity.ok().build();
    }

    //    @GetMapping("/{id}")
//    @ApiMessage("get movie")
//    public ResponseEntity<MovieDTO> getMovie(@PathVariable Long id) throws InvalidExceptions {
//        return ResponseEntity.ok(movieService.getMovie(id));
//    }
    @GetMapping("/{id}")
    @ApiMessage("get detail movie")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(@PathVariable("id") Long id) throws InvalidExceptions {
        return ResponseEntity.ok(movieService.getMovieDetail(id));
    }

    @PutMapping()
    @ApiMessage("update movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMovie(
            @Valid @ModelAttribute UpdateMovieRequest request
    ) throws InvalidExceptions, IOException {
        movieService.updateMovie(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) throws InvalidExceptions {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    @ApiMessage("get list film popular")
    public ResponseEntity<List<MovieDTO>> getPopularMovies() {
        return ResponseEntity.ok(movieService.getPopularMovies());
    }
    @GetMapping("/top-rated")
    @ApiMessage("get list film top rated")
    public ResponseEntity<List<MovieDTO>> getTopRatedMovies() {
        return ResponseEntity.ok(movieService.getTopRatedMovies());
    }
    @GetMapping("/searchByTitle")
    @ApiMessage("search by title")
    public ResponseEntity<ResultPagination> searchByTitle(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean desc
    ){
        return ResponseEntity.ok(movieService.searchByTitle(keyword,page,size,desc));
    }
}
