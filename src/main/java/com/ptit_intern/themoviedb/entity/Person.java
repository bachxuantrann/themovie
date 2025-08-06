package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person extends BaseEntity{
    @NotBlank(message = "name of person is required")
    String name;
    String profilePath;
    @Column(columnDefinition = "TEXT")
    String biography;
    LocalDate birthDate;
    String placeOfBirth;
    LocalDate deathDate;
    GenderEnum gender;
}
