package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Person extends BaseEntity<PersonDTO> {
    @NotBlank(message = "name of person is required")
    @Column(name = "name", nullable = false)
    String name;
    @Column(name="career",nullable = false)
    String career;
    @Column(name = "profile_path",columnDefinition = "TEXT")
    String profilePath;
    @Column(name = "profile_public_id",columnDefinition = "TEXT")
    String profilePublicId;
    @Column(columnDefinition = "TEXT")
    String biography;
    @Column(name = "birth_date")
    LocalDate birthDate;
    @Column(name = "place_of_birth")
    String placeOfBirth;
    @Column(name = "death_date")
    LocalDate deathDate;
    GenderEnum gender;

    //    Relationships
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCast> movieCasts = new HashSet<>();
}
