package com.ptit_intern.themoviedb.dto.dtoClass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO extends BaseDTO {
    private Long id;
    @NotNull(message = "userId is required")
    private Long userId;
    @NotBlank(message = "name of list is required")
    private String name;
    private String description;
    private Boolean isPublic=true;
}
