package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.LanguageRepository;
import com.ptit_intern.themoviedb.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    @Override
    public void createLanguage(Language language) throws InvalidExceptions {
        if (isExists(language.getLanguageCode(), language.getName())) {
            throw new InvalidExceptions("Language is existed");
        }
        Language newLanguage = new Language();
        newLanguage.setLanguageCode(language.getLanguageCode());
        newLanguage.setName(language.getName());
        languageRepository.save(newLanguage);

    }

    @Override
    public LanguageDTO getLanguage(Long id) throws InvalidExceptions {
        return this.languageRepository.findById(id).orElseThrow(
                () -> new InvalidExceptions("Language not found")
        ).toDTO(LanguageDTO.class);
    }

    @Override
    public void deleteLanguage(Long id) throws InvalidExceptions {
        if (this.languageRepository.findById(id).isPresent()) {
            this.languageRepository.deleteById(id);
        } else {
            throw new InvalidExceptions("Language not found");
        }
    }

    @Override
    public void updateLanguage(LanguageDTO languageDTO) throws InvalidExceptions {
        Language language = this.languageRepository.findById(languageDTO.getId()).orElseThrow(() -> new InvalidExceptions("language is not existed"));
        if (languageDTO.getLanguageCode() != null && !languageRepository.existsByLanguageCodeAndIdNot(languageDTO.getLanguageCode(), languageDTO.getId())) {
            language.setLanguageCode(languageDTO.getLanguageCode());
        }
        if (languageDTO.getName() != null && !languageRepository.existsByNameAndIdNot(languageDTO.getName(), languageDTO.getId())) {
            language.setName(languageDTO.getName());
        }
        languageRepository.save(language);
    }

    @Override
    public ResultPagination searchLanguages(int page, int size, String keyword, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Language> languages = languageRepository.searchLanguages(keyword, pageable);
        List<LanguageDTO> languageDTOS = new ArrayList<>();
        languageDTOS = languages.stream().map(genre -> genre.toDTO(LanguageDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(languageDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(languages.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(languages.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    private boolean isExists(String languageCode, String name) {
        return languageRepository.existsByLanguageCodeAndName(languageCode, name);
    }

}
