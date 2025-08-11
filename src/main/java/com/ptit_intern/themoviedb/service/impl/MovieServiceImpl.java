package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.repository.MovieRepository;
import com.ptit_intern.themoviedb.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
}
