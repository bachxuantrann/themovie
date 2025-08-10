package com.ptit_intern.themoviedb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UploadUserRequest {
    Long id;
    String fullName;
    String email;
    String description;
    Boolean removeAvatar;
    MultipartFile avatar;
}
