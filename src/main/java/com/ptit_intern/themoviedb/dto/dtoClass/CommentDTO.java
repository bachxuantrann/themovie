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
public class CommentDTO extends BaseDTO {
    private Long id;
    private Long userId;
    @NotNull(message = "movieId is required")
    private Long movieId;
    @NotBlank(message = "content of comment is required")
    private String content;
}
