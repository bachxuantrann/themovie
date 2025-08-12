package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.request.CreateMovieRequest;
import com.ptit_intern.themoviedb.entity.*;
import com.ptit_intern.themoviedb.repository.*;
import com.ptit_intern.themoviedb.service.MovieService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
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

    public Movie createMovie(CreateMovieRequest request) throws IOException {
        log.info("Creating new movie:{}", request.getTitle());
        Movie movie = new Movie();
        copyBasicPropertiesFromCreateRequest(request, movie);
        handleImageUploadsForCreate(movie, request);
        Movie savedMovie = movieRepository.save(movie);
        log.info("Successfully created movie with ID: {}", savedMovie.getId());
        return  savedMovie;
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
    private void handleImageUploadsForCreate(Movie movie,CreateMovieRequest request) throws IOException {
        if (request.getPoster() != null && !request.getPoster().isEmpty()) {
            UploadOptions posterOptions = new UploadOptions();
            posterOptions.setFolder("movies/posters");
            posterOptions.setTags(List.of("movie","poster"));
            var posterUploadRes = cloudinaryService.uploadFileWithPublicId(request.getPoster(), posterOptions);
            movie.setPosterPath(posterUploadRes.secureUrl());
            movie.setPosterPublicId(posterUploadRes.publicId());
        }

        // Handle backdrop upload
        if (request.getBackdrop() != null && !request.getBackdrop().isEmpty()) {
            UploadOptions backdropOptions = new UploadOptions();
            backdropOptions.setFolder("movies/backdrops");
            backdropOptions.setTags(List.of("movie","backdrop"));
            var backdropUploadRes = cloudinaryService.uploadFileWithPublicId(request.getBackdrop(), backdropOptions);
            movie.setBackdropPath(backdropUploadRes.secureUrl());
            movie.setBackdropPublicId(backdropUploadRes.publicId());
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
            movie.setMovieCompanies(movieCompanies);
        }
    }
}
