package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.GenreRepository;
import com.ptit_intern.themoviedb.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public void createGenre(Genre genre) throws InvalidExceptions {
        if (isExists(genre.getName())) {
            throw new InvalidExceptions("Genre is existed");
        }
        Genre newGenre = new Genre();
        newGenre.setName(genre.getName());
        genreRepository.save(newGenre);
    }

    @Override
    public GenreDTO getGenre(Long id) throws InvalidExceptions {
        return this.genreRepository.findById(id).orElseThrow(
                () -> new InvalidExceptions("Language not found")
        ).toDTO(GenreDTO.class);
    }

    @Override
    public void deleteGenre(Long id) throws InvalidExceptions {
        if (genreRepository.findById(id).isPresent()) {
            genreRepository.deleteById(id);
        } else {
            throw new InvalidExceptions("Genre not found");
        }
    }

    private boolean isExists(String name) {
        return genreRepository.existsByName(name);
    }
}
