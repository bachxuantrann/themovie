package com.ptit_intern.themoviedb.dto.dtoClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO extends BaseDTO {
    private Long id;
    private String name;
    private String logoPath;
    private String logoPublicId;
}
