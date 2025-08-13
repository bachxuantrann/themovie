package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.request.BulkRelationshipRequest;
import com.ptit_intern.themoviedb.dto.request.CastRequest;
import com.ptit_intern.themoviedb.dto.request.CrewRequest;
import com.ptit_intern.themoviedb.dto.request.RelationshipStatsDTO;
import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieRelationshipService {
    List<?> getRelationships(Long movieId, String type, int page, int size) throws InvalidExceptions;

    void addRelationships(Long movieId, String type, List<Long> entityIds) throws InvalidExceptions;

    void removeRelationship(Long movieId, String type, Long entityId) throws InvalidExceptions;

    void addMovieCast(Long movieId, List<CastRequest> castRequests) throws InvalidExceptions;
    void addMovieCrew(Long movieId,List<CrewRequest> crewRequests) throws InvalidExceptions;
//    Bulk operations
    void bulkAddRelationships(Long movieId, String type, BulkRelationshipRequest request) throws InvalidExceptions;
    void bulkRemoveRelationships(Long movieId,String type, List<Long> entityIds) throws InvalidExceptions;
    Page<Movie> getMoviesByEntity(String entityType, Long entityId, String job, Pageable pageable);
    RelationshipStatsDTO getRelationshipStats(Long movieId) throws InvalidExceptions;
}
