package com.ptit_intern.themoviedb.dto.dtoClass;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String username;
    String email;
    String fullName;
    RoleEnum role;
    String description;
    String avatarUrl;
    String avatarPublicId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant created_at;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant updated_at;
    String created_by;
    String updated_by;
}
