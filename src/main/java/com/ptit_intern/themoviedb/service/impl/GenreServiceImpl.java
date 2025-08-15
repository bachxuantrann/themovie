package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.GenreRepository;
import com.ptit_intern.themoviedb.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public GenreDTO updateGenre(GenreDTO genreDTO) throws InvalidExceptions {
        if (isExists(genreDTO.getName())) {
            throw new InvalidExceptions("Name of genre is existed");
        }
        Genre updateGenre = genreRepository.findById(genreDTO.getId()).orElseThrow(()-> new InvalidExceptions("Genre not found"));
        updateGenre.setName(genreDTO.getName());
        return genreRepository.save(updateGenre).toDTO(GenreDTO.class);

    }

    @Override
    public ResultPagination searchGenre(int page, int size, String keyword, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Genre> genres = genreRepository.searchGenres(keyword, pageable);
        List<GenreDTO> genreDTOS = new ArrayList<>();
        genreDTOS = genres.stream().map(genre -> genre.toDTO(GenreDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(genreDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(genres.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(genres.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    private boolean isExists(String name) {
        return genreRepository.existsByName(name);
    }
}
