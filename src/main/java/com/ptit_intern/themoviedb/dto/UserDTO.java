package com.ptit_intern.themoviedb.dto;

import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String description;
    String avatar_url;
}
