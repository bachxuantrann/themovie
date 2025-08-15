package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.dto.request.CreatePersonRequest;
import com.ptit_intern.themoviedb.entity.Person;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.PersonRepository;
import com.ptit_intern.themoviedb.service.PersonService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CloudinaryService cloudinaryService;
    private String profileDefault = "";

    @Override
    public void createPerson(CreatePersonRequest request) throws InvalidExceptions, IOException {
        if (isExisted(request.getName())) {
            throw new InvalidExceptions("Person is existed");
        }
        Person person = new Person();
        person.setName(request.getName());
        person.setCareer(request.getCareer());
        person.setBiography(request.getBiography());
        person.setBiography(request.getBiography());
        person.setBirthDate(request.getBirthDate());
        person.setPlaceOfBirth(request.getPlaceOfBirth());
        person.setDeathDate(request.getDeathDate());
        person.setGender(GenderEnum.valueOf(request.getGender().toUpperCase()));
        boolean isProfile = request.getProfile() != null && !request.getProfile().isEmpty();
        if (isProfile) {
            UploadOptions options = new UploadOptions();
            options.setFolder("persons");
            options.setTags(List.of("profile", person.getCareer()));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(request.getProfile(), options);
            String url = uploadRes.secureUrl();
            String publicId = uploadRes.publicId();
            person.setProfilePath(url);
            person.setProfilePublicId(publicId);
        }
        this.personRepository.save(person);
    }

    @Override
    public PersonDTO getPerson(Long id) throws InvalidExceptions {
        return personRepository.findById(id).orElseThrow(() -> new InvalidExceptions("user not found")).toDTO(PersonDTO.class);
    }

    @Override
    public void deletePerson(Long id) throws InvalidExceptions {
        if (!personRepository.existsById(id)) {
            throw new InvalidExceptions("Person is not existed");
        }
        personRepository.deleteById(id);
    }

    private boolean isExisted(String name) {
        return personRepository.existsByName(name);
    }
}
