package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User  extends BaseEntity<UserDTO> {
    @NotBlank(message = "username is required")
    String username;
    @NotBlank(message = "password is required")
    String password;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    String email;
    String fullName;
    @Column(columnDefinition = "MEDIUMTEXT")
    String description;
    @Column(columnDefinition = "MEDIUMTEXT")
    String avatarUrl;
    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;
}
