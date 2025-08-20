package com.ptit_intern.themoviedb.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.dto.request.AdvanceSearchRequest;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.PersonCastRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.dto.response.MovieCastInfo;
import com.ptit_intern.themoviedb.dto.response.MovieDetailResponse;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.*;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.*;
import com.ptit_intern.themoviedb.service.MovieService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CloudinaryService cloudinaryService;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MovieCountryRepository movieCountryRepository;
    private final MovieLanguageRepository movieLanguageRepository;
    private final MovieCompanyRepository movieCompanyRepository;
    private final MovieCastRepository movieCastRepository;
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;
    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;
    private final CommentRepository commentRepository;
    private final ListItemRepository listItemRepository;
    private final RatingRepository ratingRepository;
    private final UserFavouriteMovieRepository userFavouriteMovieRepository;
    private final ObjectMapper objectMapper;
    private final UserListRepository userListRepository;

    public record MovieGeneral(Long id, String title, String originalTitle, String overview, LocalDate releaseDate,
                               String posterPath, String backdropPath) {
    }

    public record PersonGeneral(Long id, String name, String career, String profilePath, String biography,
                                GenderEnum gender) {
    }

    public record MovieSearchAdvanced(Long id, String title, String originalTitle, LocalDate releaseDate,
                                      BigDecimal voteAverage, String posterPath, String backdropPath, Integer runtime) {
    }

    @Transactional(rollbackOn = {Exception.class, IOException.class})
    public void createMovie(CreateMovieRequest request) throws IOException {
        log.info("Creating new movie:{}", request.getTitle());
        if (request.getReleaseDate() != null &&
                movieRepository.existsByTitleAndReleaseDate(request.getTitle(), request.getReleaseDate())) {
            throw new RuntimeException(
                    "Movie with title '" + request.getTitle() +
                            "' and release date '" + request.getReleaseDate() + "' already exists");
        }
        Movie movie = new Movie();
        copyBasicPropertiesFromCreateRequest(request, movie);
        handleImageUploadsForCreate(movie, request);
        Movie savedMovie = movieRepository.save(movie);
        setMovieRelationships(savedMovie, request.getGenreIds(), request.getCountryIds(),
                request.getLanguageIds(), request.getCompanyIds());
        if (StringUtils.hasText(request.getPersons())) {
            processPersons(request.getPersons(), savedMovie);
        }
        log.info("Successfully created movie with ID: {}", savedMovie.getId());
    }

    @Override
    public MovieDTO getMovie(Long id) throws InvalidExceptions {
        return movieRepository.findById(id).orElseThrow(() -> new InvalidExceptions("Movie is not found")).toDTO(MovieDTO.class);

    }

    @Override
    public void updateMovie(UpdateMovieRequest request) throws InvalidExceptions, IOException {
        log.info("Updating movie:{}", request.getTitle());
        Movie movieUpdate = movieRepository.findById(request.getId()).orElseThrow(() -> new InvalidExceptions("Movie is not found" + request.getId()));
        // Check for duplicates (excluding current movie)
        if (StringUtils.hasText(request.getTitle()) && request.getReleaseDate() != null) {
            movieRepository.findByTitleAndReleaseDate(request.getTitle(), request.getReleaseDate())
                    .ifPresent(movie -> {
                        if (!movie.getId().equals(request.getId())) {
                            throw new RuntimeException(
                                    "Movie with title '" + request.getTitle() +
                                            "' and release date '" + request.getReleaseDate() + "' already exists");
                        }
                    });
        }
        updateMovieFields(movieUpdate, request);
        handleImageUploadsForUpdate(movieUpdate, request);
//      update movie relationships
        clearExistingRelationships(movieUpdate);
        movieRepository.flush();
        setMovieRelationships(movieUpdate, request.getGenreIds(), request.getCountryIds(),
                request.getLanguageIds(), request.getCompanyIds()
        );
        if (StringUtils.hasText(request.getPersons())) {
            processPersons(request.getPersons(), movieUpdate);
        }
        Movie updatedMovie = movieRepository.save(movieUpdate);
        log.info("Successfully updated movie with ID: {}", updatedMovie.getId());
    }

    @Override
    public void deleteMovie(Long id) throws InvalidExceptions {
        log.info("deleting movie:{}", id);
        Movie movie = movieRepository.findById(id).orElseThrow(
                () -> new InvalidExceptions("Movie is not existed")
        );
        try {
            deleteMovieImages(movie);
            deleteMovieRelationships(movie);
            movieRepository.deleteById(movie.getId());
            movieRepository.flush();
            log.info("Successfully deleted movie with ID: {} and title: {}", id, movie.getTitle());
        } catch (Exception e) {
            log.error("Failed to delete movie with ID: {}, error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete movie: " + e.getMessage(), e);
        }

    }

    @Override
    public MovieDetailResponse getMovieDetail(Long id) throws InvalidExceptions {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new InvalidExceptions("Movie not found with id: " + id));
        MovieDetailResponse response = new MovieDetailResponse();
        response.setMovie(movie.toDTO(MovieDTO.class));
        List<MovieCastInfo> castings = new ArrayList<>();
        List<MovieCastInfo> crews = new ArrayList<>();
        for (MovieCast movieCast : movie.getMovieCasts()) {
            MovieCastInfo castInfo = new MovieCastInfo();
            castInfo.setPersonDTO(movieCast.getPerson().toDTO(PersonDTO.class));
            castInfo.setJob(movieCast.getJob());
            castInfo.setCharacterName(!Objects.equals(movieCast.getCharacterName(), "") ? movieCast.getCharacterName() : "");
            if ("Casting".equalsIgnoreCase(movieCast.getJob())) {
                castings.add(castInfo);
            } else {
                crews.add(castInfo);
            }
        }
        response.setCasting(castings);
        response.setCrew(crews);
        return response;
    }

    @Override
    public ResultPagination advancedSearch(AdvanceSearchRequest request) throws InvalidExceptions {
        validateAdvancedSearchRequest(request);
        Specification<Movie> specification = MovieSpecification.buildSpecification(request);
        Sort sort = MovieSpecification.buildSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        Page<Movie> moviePage = movieRepository.findAll(specification, pageable);
        List<MovieSearchAdvanced> movieDTOS = moviePage.getContent().stream().map(movie ->
                new MovieSearchAdvanced(movie.getId(), movie.getTitle(), movie.getOriginalTitle(), movie.getReleaseDate(),
                        movie.getVoteAverage(), movie.getPosterPath(), movie.getBackdropPath(), movie.getRuntime())).toList();
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(moviePage.getTotalElements());
        metaInfo.setTotalPages(moviePage.getTotalPages());
        metaInfo.setPage(request.getPage());
        metaInfo.setSize(request.getSize());
        return new ResultPagination(metaInfo, movieDTOS);
    }

    @Override
    public List<MovieDTO> getPopularMovies() {
        return movieRepository.findTop10ByOrderByReleaseDateDesc().stream().map(movie -> movie.toDTO(MovieDTO.class)).toList();
    }

    @Override
    public List<MovieDTO> getTopRatedMovies() {
        return movieRepository.findTop10ByOrderByVoteAverageDesc().stream().map(movie -> movie.toDTO(MovieDTO.class)).toList();
    }

    @Override
    public ResultPagination searchByTitle(String keyword, int page, int size, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Movie> movies = movieRepository.searchMovies(keyword, pageable);
        List<MovieDTO> movieDTOS = movies.getContent().stream().map(movie -> movie.toDTO(MovieDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(movieDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotalPages(movies.getTotalPages());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotal(movies.getTotalElements());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    @Override
    public Map<String, Object> searchGeneral(String keyword, int page, int size, boolean desc) {
        Map<String, Object> result = new HashMap<>();
        if (keyword == null || keyword.isEmpty() || keyword.equals("")) {
            result.put("movies", "");
            result.put("persons", "");
            return result;
        }
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Movie> movies = movieRepository.searchMovies(keyword, pageable);
        Page<Person> persons = personRepository.searchPersonsByName(keyword, pageable);
        List<MovieGeneral> movieDTOS = movies.getContent().stream().map(
                movie -> new MovieGeneral(
                        movie.getId(), movie.getTitle(), movie.getOriginalTitle(), movie.getOverview(),
                        movie.getReleaseDate(), movie.getPosterPath(), movie.getBackdropPath()
                )).toList();
        List<PersonGeneral> personDTOS = persons.getContent().stream().map(
                person -> new PersonGeneral(
                        person.getId(), person.getName(), person.getCareer(),
                        person.getProfilePath(), person.getBiography(), person.getGender())
        ).toList();
        ResultPagination resultMovies = new ResultPagination();
        ResultPagination resultPersons = new ResultPagination();
        resultMovies.setResults(movieDTOS);
        resultPersons.setResults(personDTOS);
        ResultPagination.MetaInfo metaInfoMovies = new ResultPagination.MetaInfo();
        metaInfoMovies.setTotalPages(movies.getTotalPages());
        metaInfoMovies.setPage(page);
        metaInfoMovies.setSize(size);
        metaInfoMovies.setTotal(movies.getTotalElements());
        resultMovies.setMetaInfo(metaInfoMovies);
        ResultPagination.MetaInfo metaInfoPersons = new ResultPagination.MetaInfo();
        metaInfoPersons.setTotalPages(persons.getTotalPages());
        metaInfoPersons.setPage(page);
        metaInfoPersons.setSize(size);
        metaInfoPersons.setTotal(persons.getTotalElements());
        result.put("movies", resultMovies);
        result.put("persons", resultPersons);
        return result;
    }

    @Override
    public Map<String, Object> getStatusMovie(Long movieId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(userName);
        Long userId = user.getId();
        boolean inFavourite = userFavouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId);
        boolean inUserList = userListRepository.existsMovieInUserLists(userId, movieId);
        Optional<Rating> ratingOfUser = ratingRepository.findByUserIdAndMovieId(userId, movieId);
        boolean rated = ratingOfUser.isPresent();
        Map<String, Object> result = new HashMap<>();
        result.put("inFavourite", inFavourite);
        result.put("inUserList", inUserList);
        BigDecimal rating = rated ? ratingOfUser.get().getScore() : null;
        result.put("ratingScore", rating);
        result.put("isRated", rated);
        return result;
    }

    private void validateAdvancedSearchRequest(AdvanceSearchRequest request) throws InvalidExceptions {
//        Validate vote average range
        if (request.getMinVoteAverage() != null && request.getMaxVoteAverage() != null) {
            if (request.getMinVoteAverage().compareTo(request.getMaxVoteAverage()) > 0) {
                throw new InvalidExceptions("Min vote average cannot be greater than max vote averge");
            }
        }
//        Validate runtime range
        if (request.getMinRuntime() != null && request.getMaxRuntime() != null) {
            if (request.getMinRuntime() > request.getMaxRuntime()) {
                throw new InvalidExceptions("Min run time cannot be greater than max run time");
            }
        }
//        Validate date range
        if (request.getFromReleaseDate() != null && request.getToReleaseDate() != null) {
            if (request.getFromReleaseDate().isAfter(request.getToReleaseDate())) {
                throw new InvalidExceptions("From date cannot be after to date");
            }
        }
//       Validate sort parameters
        String[] validSortFields = {"releasedate", "voteaverage", "title"};
        String[] validSortDirections = {"asc", "desc"};

        if (request.getSortBy() == null ||
                !Arrays.asList(validSortFields).contains(request.getSortBy().toLowerCase())) {
            throw new InvalidExceptions("Invalid sort field: " + request.getSortBy());
        }
        if (request.getSortDirection() == null ||
                !Arrays.asList(validSortDirections).contains(request.getSortDirection().toLowerCase())) {
            throw new InvalidExceptions("Invalid sort direction: " + request.getSortDirection());
        }
    }

    private void processPersons(String personJson, Movie movie) throws IOException {
        try {
            log.info("Processing persons JSON for movie ID: {}", movie.getId());
            log.debug("Raw JSON: {}", personJson);
            List<PersonCastRequest> personRequests = objectMapper.readValue(
                    personJson, new TypeReference<List<PersonCastRequest>>() {
                    }
            );
            log.info("Successfully parsed {} person requests", personRequests.size());
            List<MovieCast> movieCastsToSave = new ArrayList<>();
            for (PersonCastRequest personRequest : personRequests) {
                log.debug("Processing person request: {}", personRequest);
                Person person = personRepository.findById(personRequest.getPersonId())
                        .orElseThrow(() -> new InvalidExceptions("Person is not found" + personRequest.getPersonId()));
                MovieCast movieCast = MovieCast.builder().movie(movie).person(person).job(personRequest.getJob()).build();
                if ("Casting".equalsIgnoreCase(personRequest.getJob()) && StringUtils.hasText(personRequest.getCharacterName())) {
                    movieCast.setCharacterName(personRequest.getCharacterName());
                } else {
                    movieCast.setCharacterName("");
                }
                movieCastsToSave.add(movieCast);
                log.debug("Created MovieCast: job={}, characterName={}", personRequest.getJob(), movieCast.getCharacterName());
            }
            movieCastRepository.saveAll(movieCastsToSave);
        } catch (Exception ex) {
            log.error("Failed to process persons {} from movie with ID: {}", personJson, movie.getId(), ex);
            throw new RuntimeException("Invalid person JSON format", ex);
        }
    }

    private void deleteMovieImages(Movie movie) {
        // Delete poster image
        if (movie.getPosterPublicId() != null || movie.getPosterPath() != null) {
            deleteOldImage(movie.getPosterPublicId(), movie.getPosterPath(), "poster");
        }

        // Delete backdrop image
        if (movie.getBackdropPublicId() != null || movie.getBackdropPath() != null) {
            deleteOldImage(movie.getBackdropPublicId(), movie.getBackdropPath(), "backdrop");
        }
    }

    private void deleteMovieRelationships(Movie movie) {
        clearExistingRelationships(movie);
        ratingRepository.deleteByMovieId(movie.getId());
        commentRepository.deleteByMovieId(movie.getId());
        userFavouriteMovieRepository.deleteByMovieId(movie.getId());
        listItemRepository.deleteByMovieId(movie.getId());
    }

    //    helper methods
    private void copyBasicPropertiesFromCreateRequest(CreateMovieRequest request, Movie movie) {
        movie.setTitle(request.getTitle());
        movie.setOriginalTitle(request.getOriginalTitle());
        movie.setOverview(request.getOverview());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setRuntime(request.getRuntime());
        movie.setVoteAverage(request.getVoteAverage());
        movie.setVoteCount(request.getVoteCount());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setBudget(request.getBudget());
        movie.setRevenue(request.getRevenue());
        movie.setTagline(request.getTagline());
        movie.setHomepageUrl(request.getHomepageUrl());
        movie.setStatus(request.getStatus());
    }

    private void handleImageUploadsForCreate(Movie movie, CreateMovieRequest request) throws IOException {
        if (request.getPoster() != null && !request.getPoster().isEmpty()) {
            UploadOptions posterOptions = new UploadOptions();
            posterOptions.setFolder("movies/posters");
            posterOptions.setTags(List.of("movie", "poster"));
            var posterUploadRes = cloudinaryService.uploadFileWithPublicId(request.getPoster(), posterOptions);
            movie.setPosterPath(posterUploadRes.secureUrl());
            movie.setPosterPublicId(posterUploadRes.publicId());
        }

        // Handle backdrop upload
        if (request.getBackdrop() != null && !request.getBackdrop().isEmpty()) {
            UploadOptions backdropOptions = new UploadOptions();
            backdropOptions.setFolder("movies/backdrops");
            backdropOptions.setTags(List.of("movie", "backdrop"));
            var backdropUploadRes = cloudinaryService.uploadFileWithPublicId(request.getBackdrop(), backdropOptions);
            movie.setBackdropPath(backdropUploadRes.secureUrl());
            movie.setBackdropPublicId(backdropUploadRes.publicId());
        }
    }

    private void handleImageUploadsForUpdate(Movie movie, UpdateMovieRequest request) throws IOException {
        handlePosterUpdate(movie, request);
        handleBackdropUpdate(movie, request);
    }

    private void handlePosterUpdate(Movie movie, UpdateMovieRequest request) throws IOException {
        MultipartFile newPoster = request.getPoster();
        Boolean removePoster = request.getRemovePoster();

        if (newPoster != null && !newPoster.isEmpty()) {
            // Upload new poster
            UploadOptions posterOptions = new UploadOptions();
            posterOptions.setFolder("movies/posters");
            posterOptions.setTags(List.of("movie", "poster"));

            var posterUploadRes = cloudinaryService.uploadFileWithPublicId(newPoster, posterOptions);
            String newPosterUrl = posterUploadRes.secureUrl();
            String newPosterPublicId = posterUploadRes.publicId();

            // Delete old poster if exists
            String oldPosterPublicId = movie.getPosterPublicId();
            String oldPosterUrl = movie.getPosterPath();

            // Set new poster
            movie.setPosterPath(newPosterUrl);
            movie.setPosterPublicId(newPosterPublicId);

            // Delete old poster from Cloudinary
            deleteOldImage(oldPosterPublicId, oldPosterUrl, "poster");

        } else if (Boolean.TRUE.equals(removePoster)) {
            // Remove existing poster
            String posterPublicId = movie.getPosterPublicId();
            String posterUrl = movie.getPosterPath();

            movie.setPosterPublicId(null);
            movie.setPosterPath(null);

            // Delete from Cloudinary
            deleteOldImage(posterPublicId, posterUrl, "poster");
        }
    }

    private void handleBackdropUpdate(Movie movie, UpdateMovieRequest request) throws IOException {
        MultipartFile newBackdrop = request.getBackdrop();
        Boolean removeBackdrop = request.getRemoveBackdrop();

        if (newBackdrop != null && !newBackdrop.isEmpty()) {
            // Upload new backdrop
            UploadOptions backdropOptions = new UploadOptions();
            backdropOptions.setFolder("movies/backdrops");
            backdropOptions.setTags(List.of("movie", "backdrop"));

            var backdropUploadRes = cloudinaryService.uploadFileWithPublicId(newBackdrop, backdropOptions);
            String newBackdropUrl = backdropUploadRes.secureUrl();
            String newBackdropPublicId = backdropUploadRes.publicId();

            // Delete old backdrop if exists
            String oldBackdropPublicId = movie.getBackdropPublicId();
            String oldBackdropUrl = movie.getBackdropPath();

            // Set new backdrop
            movie.setBackdropPath(newBackdropUrl);
            movie.setBackdropPublicId(newBackdropPublicId);

            // Delete old backdrop from Cloudinary
            deleteOldImage(oldBackdropPublicId, oldBackdropUrl, "backdrop");

        } else if (Boolean.TRUE.equals(removeBackdrop)) {
            // Remove existing backdrop
            String backdropPublicId = movie.getBackdropPublicId();
            String backdropUrl = movie.getBackdropPath();

            movie.setBackdropPublicId(null);
            movie.setBackdropPath(null);

            // Delete from Cloudinary
            deleteOldImage(backdropPublicId, backdropUrl, "backdrop");
        }
    }

    private void deleteOldImage(String publicId, String url, String imageType) {
        try {
            if (StringUtils.hasText(publicId)) {
                cloudinaryService.deleteImageByPublicId(publicId);
                log.info("Successfully deleted old {} with publicId: {}", imageType, publicId);
            } else if (StringUtils.hasText(url)) {
                cloudinaryService.deleteImageByUrl(url);
                log.info("Successfully deleted old {} with url: {}", imageType, url);
            }
        } catch (Exception ex) {
            log.warn("Failed to delete old {} from Cloudinary - publicId: {}, url: {}, error: {}",
                    imageType, publicId, url, ex.getMessage());
        }
    }

    private void setMovieRelationships(Movie movie, Set<Long> genreIds, Set<Long> countryIds,
                                       Set<Long> languageIds, Set<Long> companyIds) {
        // Set genres
        if (genreIds != null && !genreIds.isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(genreIds);
            Set<MovieGenre> movieGenres = new HashSet<>();
            for (Genre genre : genres) {
                MovieGenre movieGenre = new MovieGenre();
                movieGenre.setMovie(movie);
                movieGenre.setGenre(genre);
                movieGenres.add(movieGenre);
            }
            movieGenreRepository.saveAll(movieGenres);
            movie.setMovieGenres(movieGenres);
        }

        // Set countries
        if (countryIds != null && !countryIds.isEmpty()) {
            List<Country> countries = countryRepository.findAllById(countryIds);
            Set<MovieCountry> movieCountries = new HashSet<>();
            for (Country country : countries) {
                MovieCountry movieCountry = new MovieCountry();
                movieCountry.setMovie(movie);
                movieCountry.setCountry(country);
                movieCountries.add(movieCountry);
            }
            movieCountryRepository.saveAll(movieCountries);
            movie.setMovieCountries(movieCountries);
        }

        // Set languages
        if (languageIds != null && !languageIds.isEmpty()) {
            List<Language> languages = languageRepository.findAllById(languageIds);
            Set<MovieLanguage> movieLanguages = new HashSet<>();
            for (Language language : languages) {
                MovieLanguage movieLanguage = new MovieLanguage();
                movieLanguage.setMovie(movie);
                movieLanguage.setLanguage(language);
                movieLanguages.add(movieLanguage);
            }
            movieLanguageRepository.saveAll(movieLanguages);
            movie.setMovieLanguages(movieLanguages);
        }

        // Set companies
        if (companyIds != null && !companyIds.isEmpty()) {
            List<Company> companies = companyRepository.findAllById(companyIds);
            Set<MovieCompany> movieCompanies = new HashSet<>();
            for (Company company : companies) {
                MovieCompany movieCompany = new MovieCompany();
                movieCompany.setMovie(movie);
                movieCompany.setCompany(company);
                movieCompanies.add(movieCompany);
            }
            movieCompanyRepository.saveAll(movieCompanies);
            movie.setMovieCompanies(movieCompanies);
        }
    }

    private void updateMovieFields(Movie movie, UpdateMovieRequest request) {
        if (StringUtils.hasText(request.getTitle())) {
            movie.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getOriginalTitle())) {
            movie.setOriginalTitle(request.getOriginalTitle());
        }
        if (StringUtils.hasText(request.getOverview())) {
            movie.setOverview(request.getOverview());
        }
        if (request.getReleaseDate() != null) {
            movie.setReleaseDate(request.getReleaseDate());
        }
        if (request.getRuntime() != null) {
            movie.setRuntime(request.getRuntime());
        }
        if (request.getVoteAverage() != null) {
            movie.setVoteAverage(request.getVoteAverage());
        }
        if (request.getVoteCount() != null) {
            movie.setVoteCount(request.getVoteCount());
        }
        if (StringUtils.hasText(request.getTrailerUrl())) {
            movie.setTrailerUrl(request.getTrailerUrl());
        }
        if (request.getBudget() != null) {
            movie.setBudget(request.getBudget());
        }
        if (request.getRevenue() != null) {
            movie.setRevenue(request.getRevenue());
        }
        if (StringUtils.hasText(request.getTagline())) {
            movie.setTagline(request.getTagline());
        }
        if (StringUtils.hasText(request.getHomepageUrl())) {
            movie.setHomepageUrl(request.getHomepageUrl());
        }
        if (StringUtils.hasText(request.getStatus())) {
            movie.setStatus(request.getStatus());
        }
    }

    private void clearExistingRelationships(Movie movie) {
        movieCountryRepository.deleteByMovieId(movie.getId());
        movieLanguageRepository.deleteByMovieId(movie.getId());
        movieCompanyRepository.deleteByMovieId(movie.getId());
        movieCastRepository.deleteByMovieId(movie.getId());
        movieGenreRepository.deleteByMovieId(movie.getId());
    }

}
