package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.dto.request.AdvanceSearchRequest;
import com.ptit_intern.themoviedb.entity.Movie;
import com.ptit_intern.themoviedb.entity.MovieCountry;
import com.ptit_intern.themoviedb.entity.MovieGenre;
import com.ptit_intern.themoviedb.entity.MovieLanguage;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class MovieSpecification {
    public static Specification<Movie> buildSpecification(AdvanceSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search by title (case-insensitive, partial match)
            if (StringUtils.hasText(request.getTitle())) {
                String titleKeyword = "%" + request.getTitle().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), titleKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("originalTitle")), titleKeyword)
                );
                predicates.add(titlePredicate);
            }

            // Filter by genres
            if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
                Subquery<Long> genreSubquery = query.subquery(Long.class);
                Root<MovieGenre> movieGenreRoot = genreSubquery.from(MovieGenre.class);
                genreSubquery.select(movieGenreRoot.get("movie").get("id"))
                        .where(
                                criteriaBuilder.equal(movieGenreRoot.get("movie").get("id"), root.get("id")),
                                movieGenreRoot.get("genre").get("id").in(request.getGenreIds())
                        );
                predicates.add(criteriaBuilder.exists(genreSubquery));
            }

            // Filter by countries
            if (request.getCountryIds() != null && !request.getCountryIds().isEmpty()) {
                Subquery<Long> countrySubquery = query.subquery(Long.class);
                Root<MovieCountry> movieCountryRoot = countrySubquery.from(MovieCountry.class);
                countrySubquery.select(movieCountryRoot.get("movie").get("id"))
                        .where(
                                criteriaBuilder.equal(movieCountryRoot.get("movie").get("id"), root.get("id")),
                                movieCountryRoot.get("country").get("id").in(request.getCountryIds())
                        );
                predicates.add(criteriaBuilder.exists(countrySubquery));
            }

            // Filter by languages
            if (request.getLanguageIds() != null && !request.getLanguageIds().isEmpty()) {
                Subquery<Long> languageSubquery = query.subquery(Long.class);
                Root<MovieLanguage> movieLanguageRoot = languageSubquery.from(MovieLanguage.class);
                languageSubquery.select(movieLanguageRoot.get("movie").get("id"))
                        .where(
                                criteriaBuilder.equal(movieLanguageRoot.get("movie").get("id"), root.get("id")),
                                movieLanguageRoot.get("language").get("id").in(request.getLanguageIds())
                        );
                predicates.add(criteriaBuilder.exists(languageSubquery));
            }

            // Filter by vote average range
            if (request.getMinVoteAverage() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("voteAverage"), request.getMinVoteAverage()));
            }
            if (request.getMaxVoteAverage() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("voteAverage"), request.getMaxVoteAverage()));
            }

            // Filter by runtime range
            if (request.getMinRuntime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("runtime"), request.getMinRuntime()));
            }
            if (request.getMaxRuntime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("runtime"), request.getMaxRuntime()));
            }

            // Filter by release date range
            if (request.getFromReleaseDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("releaseDate"), request.getFromReleaseDate()));
            }
            if (request.getToReleaseDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("releaseDate"), request.getToReleaseDate()));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return switch (sortBy.toLowerCase()) {
            case "releasedDate", "releasedate" -> Sort.by(direction, "releaseDate");
            case "voteaverage", "vote_average" -> Sort.by(direction, "voteAverage");
            case "title" -> Sort.by(direction, "title");
            default -> Sort.by(Sort.Direction.DESC, "releaseDate"); // Default sort
        };
    }
}
