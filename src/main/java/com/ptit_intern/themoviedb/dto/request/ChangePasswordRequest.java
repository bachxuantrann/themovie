package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotNull(message = "id is required")
    private Long id;
    @NotBlank(message = "old password is required")
    private String oldPassword;
    @NotBlank(message = "new password is required")
    private String newPassword;
}
