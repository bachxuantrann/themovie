package com.ptit_intern.themoviedb.dto.response;

import com.ptit_intern.themoviedb.dto.dtoClass.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieCastInfo {
    private PersonDTO personDTO;
    private String job;
    private String characterName;
}
