package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.*;
import com.ptit_intern.themoviedb.dto.request.BulkRelationshipRequest;
import com.ptit_intern.themoviedb.dto.request.CastRequest;
import com.ptit_intern.themoviedb.dto.request.CrewRequest;
import com.ptit_intern.themoviedb.dto.request.RelationshipStatsDTO;
import com.ptit_intern.themoviedb.entity.*;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.MovieRepository;
import com.ptit_intern.themoviedb.repository.RelationshipRepositoryFactory;
import com.ptit_intern.themoviedb.service.MovieRelationshipService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MovieRelationshipServiceImpl implements MovieRelationshipService {

    private final MovieRepository movieRepository;
    private final RelationshipRepositoryFactory relationshipRepositoryFactory;

    @AllArgsConstructor
    @Getter
    public enum RelationshipType {
        GENRES("genres", "movie_genres", "genre_id", GenreDTO.class, true),
        COUNTRIES("countries", "movie_countries", "country_id", CountryDTO.class, true),
        LANGUAGES("languages", "movie_languages", "language_id", LanguageDTO.class, true),
        COMPANIES("companies", "movie_companies", "company_id", CompanyDTO.class, true),
        CAST("cast", "movie_casts", "person_id", CastDTO.class, false),
        CREW("crew", "movie_casts", "person_id", CrewDTO.class, false);

        private final String type;
        private final String tableName;
        private final String entityIdColumn;
        private final Class<?> dtoClass;
        private final boolean supportsBulkAdd;

        public static RelationshipType fromString(String type) {
            return Arrays.stream(values())
                    .filter(rt -> rt.type.equals(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid relationship type: " + type));
        }
    }

    @Override
    public List<?> getRelationships(Long movieId, String type, int page, int size) throws InvalidExceptions {
        RelationshipType relationshipType = RelationshipType.fromString(type);
        validateMovieExists(movieId);

        log.debug("Fetching {} relationships for movie {} (page: {}, size: {})", type, movieId, page, size);

        return switch (relationshipType) {
            case GENRES -> getMovieGenres(movieId, page, size);
            case COUNTRIES -> getMovieCountries(movieId, page, size);
            case LANGUAGES -> getMovieLanguages(movieId, page, size);
            case COMPANIES -> getMovieCompanies(movieId, page, size);
            case CAST -> getMovieCast(movieId, page, size);
            case CREW -> getMovieCrew(movieId, page, size);
        };
    }

    @Override
    @Transactional
    public void addRelationships(Long movieId, String type, List<Long> entityIds) throws InvalidExceptions {
        RelationshipType relationshipType = RelationshipType.fromString(type);
        validateMovieExists(movieId);

        if (!relationshipType.isSupportsBulkAdd()) {
            throw new UnsupportedOperationException(
                    "Use specific endpoint for " + type + " with additional data");
        }

        // Remove duplicates and validate entity IDs
        List<Long> uniqueEntityIds = entityIds.stream()
                .distinct()
                .collect(Collectors.toList());

        validateEntityIds(relationshipType, uniqueEntityIds);

        switch (relationshipType) {
            case GENRES -> addMovieGenres(movieId, uniqueEntityIds);
            case COUNTRIES -> addMovieCountries(movieId, uniqueEntityIds);
            case LANGUAGES -> addMovieLanguages(movieId, uniqueEntityIds);
            case COMPANIES -> addMovieCompanies(movieId, uniqueEntityIds);
        }
    }

    @Override
    @Transactional
    public void removeRelationship(Long movieId, String type, Long entityId) throws InvalidExceptions {
        RelationshipType relationshipType = RelationshipType.fromString(type);
        validateMovieExists(movieId);

        switch (relationshipType) {
            case GENRES -> removeMovieGenre(movieId, entityId);
            case COUNTRIES -> removeMovieCountry(movieId, entityId);
            case LANGUAGES -> removeMovieLanguage(movieId, entityId);
            case COMPANIES -> removeMovieCompany(movieId, entityId);
            case CAST, CREW -> removeMovieCast(movieId, entityId);
        }
    }

    @Override
    @Transactional
    public void addMovieCast(Long movieId, List<CastRequest> castRequests) throws InvalidExceptions {
        validateMovieExists(movieId);
        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieCast> casts = castRequests.stream()
                .map(request -> {
                    try {
                        validatePersonExists(request.getPersonId());
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Person person = relationshipRepositoryFactory.getPersonRepository().getReferenceById(request.getPersonId());
                    return MovieCast.builder()
                            .movie(movie)
                            .person(person)
                            .characterName(request.getCharacterName())
                            .orderIndex(request.getOrderIndex())
                            .job("actor")
                            .build();
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieCastRepository().saveAll(casts);
    }

    @Override
    @Transactional
    public void addMovieCrew(Long movieId, List<CrewRequest> crewRequests) throws InvalidExceptions {
        validateMovieExists(movieId);
        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieCast> crew = crewRequests.stream()
                .map(request -> {
                    try {
                        validatePersonExists(request.getPersonId());
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Person person = relationshipRepositoryFactory.getPersonRepository().getReferenceById(request.getPersonId());
                    return MovieCast.builder()
                            .movie(movie)
                            .person(person)
                            .job(request.getJob())
                            .build();
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieCastRepository().saveAll(crew);
    }

    @Override
    @Transactional
    public void bulkAddRelationships(Long movieId, String type, BulkRelationshipRequest request) throws InvalidExceptions {
        validateMovieExists(movieId);

        // Remove existing relationships if replace mode
        if (request.isReplaceMode()) {
            clearMovieRelationships(movieId, type);
        }

        if (!request.getEntityIds().isEmpty()) {
            addRelationships(movieId, type, request.getEntityIds());
        }
    }

    @Override
    @Transactional
    public void bulkRemoveRelationships(Long movieId, String type, List<Long> entityIds) throws InvalidExceptions {
        validateMovieExists(movieId);
        entityIds.forEach(entityId -> {
            try {
                removeRelationship(movieId, type, entityId);
            } catch (InvalidExceptions e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Page<Movie> getMoviesByEntity(String entityType, Long entityId, String job, Pageable pageable) {
        return switch (entityType.toLowerCase()) {
            case "genres" -> movieRepository.findByGenreId(entityId, pageable);
            case "countries" -> movieRepository.findByCountryId(entityId, pageable);
            case "languages" -> movieRepository.findByLanguageId(entityId, pageable);
            case "companies" -> movieRepository.findByCompanyId(entityId, pageable);
            case "persons" -> {
                if (StringUtils.hasText(job)) {
                    yield movieRepository.findByPersonIdAndJob(entityId, job, pageable);
                }
                yield movieRepository.findByPersonId(entityId, pageable);
            }
            default -> throw new IllegalArgumentException("Invalid entity type: " + entityType);
        };
    }

    @Override
    public RelationshipStatsDTO getRelationshipStats(Long movieId) throws InvalidExceptions {
        validateMovieExists(movieId);

        return RelationshipStatsDTO.builder()
                .genreCount(relationshipRepositoryFactory.getMovieGenreRepository().countByMovieId(movieId))
                .countryCount(relationshipRepositoryFactory.getMovieCountryRepository().countByMovieId(movieId))
                .languageCount(relationshipRepositoryFactory.getMovieLanguageRepository().countByMovieId(movieId))
                .companyCount(relationshipRepositoryFactory.getMovieCompanyRepository().countByMovieId(movieId))
                .castCount(relationshipRepositoryFactory.getMovieCastRepository().countByMovieIdAndJob(movieId, "actor"))
                .crewCount(relationshipRepositoryFactory.getMovieCastRepository().countByMovieIdAndJobNot(movieId, "actor"))
                .build();
    }

    // ========== PRIVATE HELPER METHODS ==========

    private List<GenreDTO> getMovieGenres(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return relationshipRepositoryFactory.getMovieGenreRepository()
                .findGenresByMovieId(movieId, pageable)
                .stream()
                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
    }

    private List<CountryDTO> getMovieCountries(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return relationshipRepositoryFactory.getMovieCountryRepository()
                .findCountriesByMovieId(movieId, pageable)
                .stream()
                .map(country -> new CountryDTO(country.getId(), country.getName(), country.getCountryCode()))
                .collect(Collectors.toList());
    }

    private List<LanguageDTO> getMovieLanguages(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return relationshipRepositoryFactory.getMovieLanguageRepository()
                .findLanguagesByMovieId(movieId, pageable)
                .stream()
                .map(language -> new LanguageDTO(language.getId(), language.getName(), language.getLanguageCode()))
                .collect(Collectors.toList());
    }

    private List<CompanyDTO> getMovieCompanies(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return relationshipRepositoryFactory.getMovieCompanyRepository()
                .findCompaniesByMovieId(movieId, pageable)
                .stream()
                .map(company -> new CompanyDTO(company.getId(), company.getName(), company.getLogoPath(),company.getLogoPublicId()))
                .collect(Collectors.toList());
    }

    private List<CastDTO> getMovieCast(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderIndex").ascending());
        return relationshipRepositoryFactory.getMovieCastRepository()
                .findCastByMovieId(movieId, pageable)
                .stream()
                .map(cast -> CastDTO.builder()
                        .id(cast.getId())
                        .personId(cast.getPerson().getId())
                        .name(cast.getPerson().getName())
                        .profilePath(cast.getPerson().getProfilePath())
                        .characterName(cast.getCharacterName())
                        .orderIndex(cast.getOrderIndex())
                        .build())
                .collect(Collectors.toList());
    }

    private List<CrewDTO> getMovieCrew(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("job", "person.name"));
        return relationshipRepositoryFactory.getMovieCastRepository()
                .findCrewByMovieId(movieId, pageable)
                .stream()
                .map(crew -> CrewDTO.builder()
                        .id(crew.getId())
                        .personId(crew.getPerson().getId())
                        .name(crew.getPerson().getName())
                        .profilePath(crew.getPerson().getProfilePath())
                        .job(crew.getJob())
                        .build())
                .collect(Collectors.toList());
    }

    // Optimized add methods with batch processing
    private void addMovieGenres(Long movieId, List<Long> genreIds) {
        // Check existing relationships to avoid duplicates
        Set<Long> existingGenreIds = relationshipRepositoryFactory.getMovieGenreRepository()
                .findExistingGenreIdsByMovieId(movieId);

        List<Long> newGenreIds = genreIds.stream()
                .filter(id -> !existingGenreIds.contains(id))
                .collect(Collectors.toList());

        if (newGenreIds.isEmpty()) {
            return;
        }

        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieGenre> movieGenres = newGenreIds.stream()
                .map(genreId -> {
                    try {
                        validateGenreExists(genreId);
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Genre genre = relationshipRepositoryFactory.getGenreRepository().getReferenceById(genreId);
                    return new MovieGenre(movie, genre);
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieGenreRepository().saveAll(movieGenres);
    }

    private void addMovieCountries(Long movieId, List<Long> countryIds) {
        Set<Long> existingCountryIds = relationshipRepositoryFactory.getMovieCountryRepository()
                .findExistingCountryIdsByMovieId(movieId);

        List<Long> newCountryIds = countryIds.stream()
                .filter(id -> !existingCountryIds.contains(id))
                .collect(Collectors.toList());

        if (newCountryIds.isEmpty()) {
            return;
        }

        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieCountry> movieCountries = newCountryIds.stream()
                .map(countryId -> {
                    try {
                        validateCountryExists(countryId);
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Country country = relationshipRepositoryFactory.getCountryRepository().getReferenceById(countryId);
                    return new MovieCountry(movie, country);
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieCountryRepository().saveAll(movieCountries);
    }

    private void addMovieLanguages(Long movieId, List<Long> languageIds) {
        Set<Long> existingLanguageIds = relationshipRepositoryFactory.getMovieLanguageRepository()
                .findExistingLanguageIdsByMovieId(movieId);

        List<Long> newLanguageIds = languageIds.stream()
                .filter(id -> !existingLanguageIds.contains(id))
                .collect(Collectors.toList());

        if (newLanguageIds.isEmpty()) {
            return;
        }

        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieLanguage> movieLanguages = newLanguageIds.stream()
                .map(languageId -> {
                    try {
                        validateLanguageExists(languageId);
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Language language = relationshipRepositoryFactory.getLanguageRepository().getReferenceById(languageId);
                    return new MovieLanguage(movie, language);
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieLanguageRepository().saveAll(movieLanguages);
    }

    private void addMovieCompanies(Long movieId, List<Long> companyIds) {
        Set<Long> existingCompanyIds = relationshipRepositoryFactory.getMovieCompanyRepository()
                .findExistingCompanyIdsByMovieId(movieId);

        List<Long> newCompanyIds = companyIds.stream()
                .filter(id -> !existingCompanyIds.contains(id))
                .collect(Collectors.toList());

        if (newCompanyIds.isEmpty()) {
            return;
        }

        Movie movie = movieRepository.getReferenceById(movieId);

        List<MovieCompany> movieCompanies = newCompanyIds.stream()
                .map(companyId -> {
                    try {
                        validateCompanyExists(companyId);
                    } catch (InvalidExceptions e) {
                        throw new RuntimeException(e);
                    }
                    Company company = relationshipRepositoryFactory.getCompanyRepository().getReferenceById(companyId);
                    return new MovieCompany(movie, company);
                })
                .collect(Collectors.toList());

        relationshipRepositoryFactory.getMovieCompanyRepository().saveAll(movieCompanies);
    }

    // Remove methods
    private void removeMovieGenre(Long movieId, Long genreId) {
        relationshipRepositoryFactory.getMovieGenreRepository().deleteByMovieIdAndGenreId(movieId, genreId);
    }

    private void removeMovieCountry(Long movieId, Long countryId) {
        relationshipRepositoryFactory.getMovieCountryRepository().deleteByMovieIdAndCountryId(movieId, countryId);
    }

    private void removeMovieLanguage(Long movieId, Long languageId) {
        relationshipRepositoryFactory.getMovieLanguageRepository().deleteByMovieIdAndLanguageId(movieId, languageId);
    }

    private void removeMovieCompany(Long movieId, Long companyId) {
        relationshipRepositoryFactory.getMovieCompanyRepository().deleteByMovieIdAndCompanyId(movieId, companyId);
    }

    private void removeMovieCast(Long movieId, Long castId) {
        relationshipRepositoryFactory.getMovieCastRepository().deleteByIdAndMovieId(castId, movieId);
    }

    private void clearMovieRelationships(Long movieId, String type) {
        RelationshipType relationshipType = RelationshipType.fromString(type);

        switch (relationshipType) {
            case GENRES -> relationshipRepositoryFactory.getMovieGenreRepository().deleteByMovieId(movieId);
            case COUNTRIES -> relationshipRepositoryFactory.getMovieCountryRepository().deleteByMovieId(movieId);
            case LANGUAGES -> relationshipRepositoryFactory.getMovieLanguageRepository().deleteByMovieId(movieId);
            case COMPANIES -> relationshipRepositoryFactory.getMovieCompanyRepository().deleteByMovieId(movieId);
            case CAST -> relationshipRepositoryFactory.getMovieCastRepository().deleteByMovieIdAndJob(movieId, "actor");
            case CREW ->
                    relationshipRepositoryFactory.getMovieCastRepository().deleteByMovieIdAndJobNot(movieId, "actor");
        }
    }

    // Validation methods
    private void validateMovieExists(Long movieId) throws InvalidExceptions {
        if (!movieRepository.existsById(movieId)) {
            throw new InvalidExceptions("Movie not found with id: " + movieId);
        }
    }

    @SneakyThrows
    private void validateEntityIds(RelationshipType type, List<Long> entityIds) {
        switch (type) {
            case GENRES:
                for (Long id : entityIds) {
                    validateGenreExists(id);
                }
                break;
            case COUNTRIES:
                for (Long id : entityIds) {
                    validateCountryExists(id);
                }
                break;
            case LANGUAGES:
                for (Long id : entityIds) {
                    validateLanguageExists(id);
                }
                break;
            case COMPANIES:
                for (Long id : entityIds) {
                    validateCompanyExists(id);
                }
                break;
        }
    }


    private void validateGenreExists(Long genreId) throws InvalidExceptions {
        if (!relationshipRepositoryFactory.getGenreRepository().existsById(genreId)) {
            throw new InvalidExceptions("Genre not found with id: " + genreId);
        }
    }

    private void validateCountryExists(Long countryId) throws InvalidExceptions {
        if (!relationshipRepositoryFactory.getCountryRepository().existsById(countryId)) {
            throw new InvalidExceptions("Country not found with id: " + countryId);
        }
    }

    private void validateLanguageExists(Long languageId) throws InvalidExceptions {
        if (!relationshipRepositoryFactory.getLanguageRepository().existsById(languageId)) {
            throw new InvalidExceptions("Language not found with id: " + languageId);
        }
    }

    private void validateCompanyExists(Long companyId) throws InvalidExceptions {
        if (!relationshipRepositoryFactory.getCompanyRepository().existsById(companyId)) {
            throw new InvalidExceptions("Company not found with id: " + companyId);
        }
    }

    private void validatePersonExists(Long personId) throws InvalidExceptions {
        if (!relationshipRepositoryFactory.getPersonRepository().existsById(personId)) {
            throw new InvalidExceptions("Person not found with id: " + personId);
        }
    }
}