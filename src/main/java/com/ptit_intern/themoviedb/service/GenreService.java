package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

public interface GenreService {
    void createGenre(Genre genre) throws InvalidExceptions;
    GenreDTO getGenre(Long id) throws InvalidExceptions;
    void deleteGenre(Long id)  throws InvalidExceptions;

    GenreDTO updateGenre(GenreDTO genreDTO) throws InvalidExceptions;

    ResultPagination searchGenre(int page, int size, String keyword, boolean desc);
}
