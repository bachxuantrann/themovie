package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePersonRequest {
    @NotNull
    Long id;
    @NotBlank(message = "name of person is required")
    String name;
    @NotBlank(message = "career of person is required")
    String career;
    String biography;
    LocalDate birthDate;
    String placeOfBirth;
    LocalDate deathDate;
    String gender;
    MultipartFile profile;
    boolean removeProfile=false;
}
