package com.ptit_intern.themoviedb.dto.dtoClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrewDTO {
    private Long id;
    private Long personId;
    private String name;
    private String profilePath;
    private String job;
}
