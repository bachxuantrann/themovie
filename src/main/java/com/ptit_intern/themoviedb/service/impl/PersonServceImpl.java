package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.dto.request.CreatePersonRequest;
import com.ptit_intern.themoviedb.dto.request.UpdatePersonRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Person;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.PersonRepository;
import com.ptit_intern.themoviedb.service.PersonService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CloudinaryService cloudinaryService;
    private String profileDefault = "https://res.cloudinary.com/dpioj21ib/image/upload/v1755243757/avatar-person-default_jjwuyu.jpg";

    @Override
    public void createPerson(CreatePersonRequest request) throws InvalidExceptions, IOException {
        if (isExisted(request.getName())) {
            throw new InvalidExceptions("Person is existed");
        }
        Person person = new Person();
        transferDataField(person, request.getName(), request.getCareer(), request.getBiography(), request.getBirthDate(), request.getPlaceOfBirth(), request.getDeathDate(), request.getGender());
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
        } else {
            person.setProfilePath(profileDefault);
            person.setProfilePublicId(cloudinaryService.extractPublicIdFromUrl(profileDefault));
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

    @Override
    public void updatePerson(UpdatePersonRequest request) throws InvalidExceptions, IOException {
        Person person = this.personRepository.findById(request.getId()).orElseThrow(
                () -> new InvalidExceptions("Person is not existed")
        );
        transferDataField(person, request.getName(), request.getCareer(), request.getBiography(), request.getBirthDate(), request.getPlaceOfBirth(), request.getDeathDate(), request.getGender());
        boolean isProfile = request.getProfile() != null && !request.getProfile().isEmpty();
        boolean isRemove = request.isRemoveProfile();
        String oldUrl = person.getProfilePath();
        String oldPublicId = person.getProfilePublicId();
        if (isProfile){
            UploadOptions options = new UploadOptions();
            options.setFolder("persons");
            options.setTags(List.of("profile", person.getCareer()));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(request.getProfile(),options);
            String newUrl = uploadRes.secureUrl();
            String newPublicId = uploadRes.publicId();
            person.setProfilePath(newUrl);
            person.setProfilePublicId(newPublicId);
            if (isRemove){
                try{
                    if (oldPublicId!=null && !oldPublicId.isEmpty()){
                        cloudinaryService.deleteImageByPublicId(oldPublicId);
                    } else if (oldUrl!=null && !oldUrl.isEmpty()){
                        cloudinaryService.deleteImageByUrl(oldUrl);
                    }
                } catch (Exception e){
                    log.warn("Lỗi không thể xoá ảnh trên Cloudinary person.id: {} url:{} public_id: {}", request.getId(), oldUrl,oldPublicId);
                }
            }
        } else if (isRemove) {
            try{
                if (oldPublicId!=null && !oldPublicId.isEmpty()){
                    cloudinaryService.deleteImageByPublicId(oldPublicId);
                } else if (oldUrl!=null && !oldUrl.isEmpty()){
                    cloudinaryService.deleteImageByUrl(oldUrl);
                }
            } catch (Exception e){
                log.warn("Lỗi không thể xoá ảnh trên Cloudinary person.id: {} url:{} public_id: {}", request.getId(), oldUrl,oldPublicId);
            }
            person.setProfilePath(profileDefault);
            person.setProfilePublicId(cloudinaryService.extractPublicIdFromUrl(profileDefault));
        }
        personRepository.save(person);
    }

    @Override
    public ResultPagination searchPersons(int page, int size, String keyword, String career, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Person> persons = personRepository.searchPersons(keyword,career,pageable);
        List<PersonDTO> personDTOS = persons.stream().map(person -> person.toDTO(PersonDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(personDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotalPages(persons.getTotalPages());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotal(persons.getTotalElements());
        resultPagination.setMetaInfo(metaInfo);
        return  resultPagination;
    }

    private void transferDataField(Person person, String name, String career, String biography, LocalDate birthDate, String placeOfBirth, LocalDate deathDate, String gender) {
        person.setName(name);
        person.setCareer(career);
        person.setBiography(biography != null ? biography : person.getBiography());
        person.setBirthDate(birthDate != null ? birthDate : person.getBirthDate());
        person.setPlaceOfBirth(placeOfBirth != null ? placeOfBirth : person.getPlaceOfBirth());
        person.setDeathDate(deathDate != null ? deathDate : person.getDeathDate());
        person.setGender(gender != null ?  (GenderEnum.valueOf(gender.toUpperCase())) : person.getGender());
    }

    private boolean isExisted(String name) {
        return personRepository.existsByName(name);
    }
}
