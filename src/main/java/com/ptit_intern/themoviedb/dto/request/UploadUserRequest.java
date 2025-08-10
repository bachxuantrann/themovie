package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UploadUserRequest {
    @NotBlank(message = "Id of user is required")
    Long id;
    String fullName;
    String email;
    String description;
}
