package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CastRequest {
    @NotNull(message = "Person ID is required")
    @Min(value = 1, message = "Person ID must be positive")
    private Long personId;

    @NotBlank(message = "Character name is required")
    @Size(max = 255, message = "Character name must not exceed 255 characters")
    private String characterName;

    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}
