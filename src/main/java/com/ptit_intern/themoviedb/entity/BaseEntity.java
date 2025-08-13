package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.*;
import com.ptit_intern.themoviedb.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity<DTO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;
    protected Instant created_at;
    protected Instant updated_at;
    protected String created_by;
    protected String updated_by;

    @PrePersist
    public void handleBeforeCreate() {
        this.created_at = Instant.now();
        this.created_by = SecurityUtil.getCurrentUserLogin().get();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updated_at = Instant.now();
        this.updated_by = SecurityUtil.getCurrentUserLogin().get();
    }

    public DTO toDTO(Class<DTO> clazz) {
        try {
            // Create new instance of DTO
            DTO dto = clazz.getDeclaredConstructor().newInstance();

            // Get array fields of DTO
            Field[] dtoFields = clazz.getDeclaredFields();

            // Get array fields of entity
            Field[] entityFields = getAllFields(this.getClass());

            for (Field dtoField : dtoFields) {
                dtoField.setAccessible(true);

                // Handle special mappings for Movie entity
                if (this instanceof Movie) {
                    if (handleMovieSpecialMappings((Movie) this, dto, dtoField)) {
                        continue;
                    }
                }

                // Handle regular field mapping
                for (Field entityField : entityFields) {
                    entityField.setAccessible(true);
                    if (shouldMapField(dtoField, entityField)) {
                        Object value = entityField.get(this);
                        dtoField.set(dto, value);
                        break;
                    }
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to DTO", e);
        }
    }

    private boolean shouldMapField(Field dtoField, Field entityField) {
        // Map fields with same name and type
        if (dtoField.getName().equals(entityField.getName()) &&
                dtoField.getType().equals(entityField.getType())) {
            return true;
        }

        // Map created_at to createdAt
        if (dtoField.getName().equals("createdAt") && entityField.getName().equals("created_at")) {
            return true;
        }

        // Map updated_at to updatedAt
        if (dtoField.getName().equals("updatedAt") && entityField.getName().equals("updated_at")) {
            return true;
        }

        // Map created_by to createdBy
        if (dtoField.getName().equals("createdBy") && entityField.getName().equals("created_by")) {
            return true;
        }

        // Map updated_by to updatedBy
        if (dtoField.getName().equals("updatedBy") && entityField.getName().equals("updated_by")) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean handleMovieSpecialMappings(Movie movie, DTO dto, Field dtoField) throws Exception {
        switch (dtoField.getName()) {
            case "genres":
                if (movie.getMovieGenres() != null) {
                    Set<GenreDTO> genreDTOs = movie.getMovieGenres().stream()
                            .map(mg -> mg.getGenre())
                            .filter(Objects::nonNull)
                            .map(genre -> {
                                GenreDTO genreDTO = GenreDTO.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build();
                                // Map audit fields from BaseEntity
                                mapAuditFields(genre, genreDTO);
                                return genreDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, genreDTOs);
                }
                return true;

            case "countries":
                if (movie.getMovieCountries() != null) {
                    Set<CountryDTO> countryDTOs = movie.getMovieCountries().stream()
                            .map(mc -> mc.getCountry())
                            .filter(Objects::nonNull)
                            .map(country -> {
                                CountryDTO countryDTO = CountryDTO.builder()
                                        .id(country.getId())
                                        .name(country.getName())
                                        .countryCode(country.getCountryCode())
                                        .build();
                                // Map audit fields from BaseEntity
                                mapAuditFields(country, countryDTO);
                                return countryDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, countryDTOs);
                }
                return true;

            case "languages":
                if (movie.getMovieLanguages() != null) {
                    Set<LanguageDTO> languageDTOs = movie.getMovieLanguages().stream()
                            .map(ml -> ml.getLanguage())
                            .filter(Objects::nonNull)
                            .map(language -> {
                                LanguageDTO languageDTO = LanguageDTO.builder()
                                        .id(language.getId())
                                        .name(language.getName())
                                        .languageCode(language.getLanguageCode())
                                        .build();
                                // Map audit fields from BaseEntity
                                mapAuditFields(language, languageDTO);
                                return languageDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, languageDTOs);
                }
                return true;

            case "companies":
                if (movie.getMovieCompanies() != null) {
                    Set<CompanyDTO> companyDTOs = movie.getMovieCompanies().stream()
                            .map(mc -> mc.getCompany())
                            .filter(Objects::nonNull)
                            .map(company -> {
                                CompanyDTO companyDTO = CompanyDTO.builder()
                                        .id(company.getId())
                                        .name(company.getName())
                                        .logoPath(company.getLogoPath())
                                        .build();
                                // Map audit fields from BaseEntity
                                mapAuditFields(company, companyDTO);
                                return companyDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, companyDTOs);
                }
                return true;

            case "persons":
                if (movie.getMovieCasts() != null) {
                    Set<PersonDTO> personDTOs = movie.getMovieCasts().stream()
                            .map(mc -> mc.getPerson())
                            .filter(Objects::nonNull)
                            .map(person -> {
                                PersonDTO personDTO = new PersonDTO();
                                personDTO.setName(person.getName());
                                personDTO.setProfilePath(person.getProfilePath());
                                personDTO.setProfilePublicId(person.getProfilePublicId());
                                personDTO.setBiography(person.getBiography());
                                personDTO.setBirthDate(person.getBirthDate());
                                personDTO.setPlaceOfBirth(person.getPlaceOfBirth());
                                personDTO.setDeathDate(person.getDeathDate());
                                personDTO.setGender(person.getGender());
                                // Map audit fields from BaseEntity
                                mapAuditFields(person, personDTO);
                                return personDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, personDTOs);
                }
                return true;

            case "comments":
                if (movie.getComments() != null) {
                    Set<CommentDTO> commentDTOs = movie.getComments().stream()
                            .filter(Objects::nonNull)
                            .map(comment -> {
                                CommentDTO commentDTO = new CommentDTO();
                                commentDTO.setUserId(comment.getUserId());
                                commentDTO.setMovieId(comment.getMovieId());
                                commentDTO.setContent(comment.getContent());
                                // Map audit fields from BaseEntity
                                mapAuditFields(comment, commentDTO);
                                return commentDTO;
                            })
                            .collect(Collectors.toSet());
                    dtoField.set(dto, commentDTOs);
                }
                return true;

            default:
                return false;
        }
    }

    public static Field[] getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * Map audit fields from BaseEntity to BaseDTO
     */
    private void mapAuditFields(Object sourceEntity, Object targetDTO) {
        try {
            // Check if source entity is a BaseEntity
            if (!(sourceEntity instanceof BaseEntity)) {
                return;
            }

            BaseEntity<?> baseEntity = (BaseEntity<?>) sourceEntity;

            // Get target DTO class
            Class<?> dtoClass = targetDTO.getClass();

            // Map audit fields using reflection
            mapField(baseEntity.getCreated_at(), targetDTO, dtoClass, "created_at");
            mapField(baseEntity.getUpdated_at(), targetDTO, dtoClass, "updated_at");
            mapField(baseEntity.getCreated_by(), targetDTO, dtoClass, "created_by");
            mapField(baseEntity.getUpdated_by(), targetDTO, dtoClass, "updated_by");

        } catch (Exception e) {
            // Log error but don't throw exception to avoid breaking the main flow
            System.err.println("Error mapping audit fields: " + e.getMessage());
        }
    }

    /**
     * Helper method to map individual field
     */
    private void mapField(Object value, Object targetDTO, Class<?> dtoClass, String fieldName) {
        try {
            Field field = findField(dtoClass, fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(targetDTO, value);
            }
        } catch (Exception e) {
            // Ignore individual field mapping errors
        }
    }

    /**
     * Find field in class hierarchy
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
}