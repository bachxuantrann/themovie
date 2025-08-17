package com.ptit_intern.themoviedb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonCastRequest {
    private Long personId;
    private String characterName;
    private String job;
}
