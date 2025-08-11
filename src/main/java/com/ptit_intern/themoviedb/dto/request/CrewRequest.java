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
public class CrewRequest {
    @NotNull(message = "Person ID is required")
    @Min(value = 1, message = "Person ID must be positive")
    private Long personId;

    @NotBlank(message = "Job is required")
    @Size(max = 100, message = "Job must not exceed 100 characters")
    private String job;
}
