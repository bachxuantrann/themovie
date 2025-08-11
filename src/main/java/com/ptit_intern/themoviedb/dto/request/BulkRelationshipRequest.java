package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkRelationshipRequest {
    @NotEmpty(message = "Entity IDs list cannot be empty")
    @Valid
    private List<@Min(1) Long> entityIds;

    private boolean replaceMode = false;
}
