package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import com.ptit_intern.themoviedb.entity.Language;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.LanguageRepository;
import com.ptit_intern.themoviedb.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    @Override
    public void createLanguage(Language language) throws InvalidExceptions {
        if(isExists(language.getLanguageCode(),language.getName())){
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
    private boolean isExists(String languageCode,String name){
        return languageRepository.existsByLanguageCodeAndName(languageCode,name);
    }

}
