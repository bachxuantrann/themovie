package com.ptit_intern.themoviedb.dto.dtoClass;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDTO extends BaseDTO{
    @NotNull
    private Long id;
    private String name;
    private String languageCode;
}
