package com.ptit_intern.themoviedb.dto.dtoClass;

import com.ptit_intern.themoviedb.util.enums.GenderEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonDTO extends BaseDTO {
    Long id;
    String name;
    String profilePath;
    String profilePublicId;
    String biography;
    LocalDate birthDate;
    String placeOfBirth;
    LocalDate deathDate;
    GenderEnum gender;
}
