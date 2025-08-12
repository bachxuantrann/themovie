package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.request.BulkRelationshipRequest;
import com.ptit_intern.themoviedb.dto.request.CastRequest;
import com.ptit_intern.themoviedb.dto.request.CrewRequest;
import com.ptit_intern.themoviedb.dto.request.RelationshipStatsDTO;
import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.MovieRelationshipService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies-relationship")
@Slf4j
@Validated
@RequiredArgsConstructor
public class MovieRelationshipController {
    private final MovieRelationshipService movieRelationshipService;

//   Get movie relationships by movieId
    @GetMapping("/{movieId}/{type}")
    @ApiMessage("get movie relationships")
    public ResponseEntity<List<?>> getMovieRelationships(
            @PathVariable Long movieId,
            @PathVariable @Pattern(regexp = "^(genres|countries|languages|companies|cast|crew)$", message = "Invalid relationship type") String type,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) throws InvalidExceptions {
        log.info("Getting relationships for movieId={} and type={}", movieId, type);
        List<?> relationships = movieRelationshipService.getRelationships(movieId, type, page, size);
        return ResponseEntity.ok(relationships);
    }
//  Add list relationships for movie
    @PostMapping("/{movieId}/{type}")
    @ApiMessage("add movie relationships")
    public ResponseEntity<Void> addMovieRelationships(
            @PathVariable @Min(1) Long movieId,
            @PathVariable @Pattern(regexp = "^(genres|countries|languages|companies)$") String type,
            @RequestBody @Valid @NotEmpty List<@Min(1) Long> entityIds) throws InvalidExceptions {

        log.info("Adding {} relationships to movie {}", type, movieId);
        movieRelationshipService.addRelationships(movieId, type, entityIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{movieId}/cast")
    @ApiMessage("add movie cast")
    public ResponseEntity<Void> addMovieCast(
            @PathVariable @Min(1) Long movieId,
            @RequestBody @Valid List<CastRequest> castRequests) throws InvalidExceptions {

        log.info("Adding cast to movie {}", movieId);
        movieRelationshipService.addMovieCast(movieId, castRequests);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{movieId}/crew")
    @ApiMessage("add movie crew")
    public ResponseEntity<Void> addMovieCrew(
            @PathVariable @Min(1) Long movieId,
            @RequestBody @Valid List<CrewRequest> crewRequests) throws InvalidExceptions {

        log.info("Adding crew to movie {}", movieId);
        movieRelationshipService.addMovieCrew(movieId, crewRequests);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{movieId}/{type}/{entityId}")
    @ApiMessage("remove movie relationship")
    public ResponseEntity<Void> removeMovieRelationship(
            @PathVariable @Min(1) Long movieId,
            @PathVariable String type,
            @PathVariable @Min(1) Long entityId) throws InvalidExceptions {

        log.info("Removing {} relationship {} from movie {}", type, entityId, movieId);
        movieRelationshipService.removeRelationship(movieId, type, entityId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{entityType}/{entityId}")
    @ApiMessage("get list movie by movie relationship")
    public ResponseEntity<Page<Movie>> getMoviesByEntity(
            @PathVariable @Pattern(regexp = "^(genres|countries|languages|companies|persons)$") String entityType,
            @PathVariable @Min(1) Long entityId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) String job) {

        log.info("Getting movies for {} {}", entityType, entityId);
        Page<Movie> movies = movieRelationshipService.getMoviesByEntity(
                entityType, entityId, job, PageRequest.of(page, size));
        return ResponseEntity.ok(movies);
    }

    // Bulk operations
//    Bulk add list relationships for movie
    @PostMapping("/{movieId}/{type}/bulk")
    @ApiMessage("Add list relationships for movie")
    public ResponseEntity<Void> bulkAddRelationships(
            @PathVariable @Min(1) Long movieId,
            @PathVariable String type,
            @RequestBody @Valid BulkRelationshipRequest request) throws InvalidExceptions {

        log.info("Bulk adding {} relationships to movie {}", type, movieId);
        movieRelationshipService.bulkAddRelationships(movieId, type, request);
        return ResponseEntity.ok().build();
    }

    //   Bulk remove list relationships from movie
    @DeleteMapping("/{movieId}/{type}/bulk")
    @ApiMessage("Remove list relationships from movie")
    public ResponseEntity<Void> bulkRemoveRelationships(
            @PathVariable @Min(1) Long movieId,
            @PathVariable String type,
            @RequestBody @Valid List<@Min(1) Long> entityIds) throws InvalidExceptions {

        log.info("Bulk removing {} relationships from movie {}", type, movieId);
        movieRelationshipService.bulkRemoveRelationships(movieId, type, entityIds);
        return ResponseEntity.ok().build();
    }

    // Get relationship statistics
    @GetMapping("/{movieId}/stats")
    public ResponseEntity<RelationshipStatsDTO> getRelationshipStats(
            @PathVariable @Min(1) Long movieId) throws InvalidExceptions {

        RelationshipStatsDTO stats = movieRelationshipService.getRelationshipStats(movieId);
        return ResponseEntity.ok(stats);
    }
}
