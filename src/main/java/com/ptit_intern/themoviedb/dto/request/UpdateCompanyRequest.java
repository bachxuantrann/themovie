package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompanyRequest {
    @NotNull(message = "id of company is required")
    private Long id;
    @NotBlank(message = "name of company is required")
    private String name;
    private MultipartFile logo;
    private boolean removeLogo = false;
}
