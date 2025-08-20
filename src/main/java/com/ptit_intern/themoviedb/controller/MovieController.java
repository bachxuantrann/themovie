package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.AdvanceSearchRequest;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @GetMapping("/searchGeneral")
    @ApiMessage("search general")
    public ResponseEntity<Map<String,Object>> searchGeneral(
            @RequestParam(defaultValue = "",required = false) String keyword,
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "true") boolean desc
    ) {
        return ResponseEntity.ok(movieService.searchGeneral(keyword,page,size,desc));
    }
    @GetMapping("/search")
    @ApiMessage("advanced search movies")
    public ResponseEntity<ResultPagination> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Set<Long> genreIds,
            @RequestParam(required = false) Set<Long> countryIds,
            @RequestParam(required = false) Set<Long> languageIds,
            @RequestParam(required = false) BigDecimal minVoteAverage,
            @RequestParam(required = false) BigDecimal maxVoteAverage,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromReleaseDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toReleaseDate,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws InvalidExceptions {
        AdvanceSearchRequest request = new AdvanceSearchRequest(
                title, genreIds, countryIds, languageIds,
                minVoteAverage, maxVoteAverage, minRuntime, maxRuntime,
                fromReleaseDate, toReleaseDate, sortBy, sortDirection, page, size
        );
        return ResponseEntity.ok(movieService.advancedSearch(request));
    }
    @GetMapping("/{movieId}/account_states")
    @ApiMessage("get status of movie")
    public ResponseEntity<Map<String,Object>> getStatusMovie(@PathVariable("movieId") Long movieId) throws InvalidExceptions {
        return ResponseEntity.ok(movieService.getStatusMovie(movieId));
    }
    @GetMapping("/recommendation/{id}")
    @ApiMessage("get list recommendation of movie")
    public ResponseEntity<?> getRecommendationMovie(@PathVariable Long id,
        @RequestParam(defaultValue = "1")int page,
        @RequestParam(defaultValue = "10")int size,
        @RequestParam(defaultValue = "true") boolean desc
    ) throws InvalidExceptions {
        return ResponseEntity.ok(movieService.getRecommendationMovie(id,page,size,desc));
    }
}
