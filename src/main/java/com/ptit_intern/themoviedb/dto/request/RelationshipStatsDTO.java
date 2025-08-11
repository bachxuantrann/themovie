package com.ptit_intern.themoviedb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipStatsDTO {
    private Long genreCount;
    private Long countryCount;
    private Long languageCount;
    private Long companyCount;
    private Long castCount;
    private Long crewCount;
}
