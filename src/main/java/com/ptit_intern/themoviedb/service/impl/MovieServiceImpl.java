package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateMovieRequest;
import com.ptit_intern.themoviedb.entity.*;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.*;
import com.ptit_intern.themoviedb.service.MovieService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CloudinaryService cloudinaryService;
    private final GenreRepository genreRepository;
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

    @Transactional
    public MovieDTO createMovie(CreateMovieRequest request) throws IOException {
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
        setMovieRelationships(movie, request.getGenreIds(), request.getCountryIds(),
                request.getLanguageIds(), request.getCompanyIds(),
                request.getPersonIds());
        Movie savedMovie = movieRepository.save(movie);
        log.info("Successfully created movie with ID: {}", savedMovie.getId());
        return (MovieDTO) savedMovie.toDTO(MovieDTO.class);
    }

    @Override
    public MovieDTO getMovie(Long id) throws InvalidExceptions {
        return movieRepository.findById(id).orElseThrow(() -> new InvalidExceptions("Movie is not found")).toDTO(MovieDTO.class);

    }

    @Override
    public MovieDTO updateMovie(UpdateMovieRequest request) throws InvalidExceptions, IOException {
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
                request.getLanguageIds(), request.getCompanyIds(),
                request.getPersonIds());
        Movie updatedMovie = movieRepository.save(movieUpdate);
        log.info("Successfully updated movie with ID: {}", updatedMovie.getId());
        return updatedMovie.toDTO(MovieDTO.class);
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
                                       Set<Long> languageIds, Set<Long> companyIds, Set<Long> personIds) {
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
        if (personIds != null && !personIds.isEmpty()) {
            List<Person> persons = personRepository.findAllById(personIds);
            Set<MovieCast> movieCasts = new HashSet<>();
            for (Person person : persons) {
                MovieCast movieCast = new MovieCast();
                movieCast.setMovie(movie);
                movieCast.setPerson(person);
                // Có thể set thêm các thuộc tính khác như character, role, order nếu cần
                movieCasts.add(movieCast);
            }
            movieCastRepository.saveAll(movieCasts);
            movie.setMovieCasts(movieCasts);
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
