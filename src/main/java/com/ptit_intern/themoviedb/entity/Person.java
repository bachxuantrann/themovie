package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person extends BaseEntity {
    @NotBlank(message = "name of person is required")
    String name;
    @Column(name = "profile_path")
    String profilePath;
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
