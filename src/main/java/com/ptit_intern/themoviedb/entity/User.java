package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User  extends BaseEntity<UserDTO> {
    @NotBlank(message = "username is required")
    @Column(name = "username", length = 100, unique = true, nullable = false)
    String username;
    @NotBlank(message = "password is required")
    @Column(name = "password", nullable = false)
    String password;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    String email;
    @Column(name = "full_name")
    String fullName;
    @Column(columnDefinition = "MEDIUMTEXT")
    String description;
    @Column(columnDefinition = "MEDIUMTEXT",name = "avatar_url")
    String avatarUrl;
    @Column(columnDefinition = "MEDIUMTEXT",name = "avatar_public_id")
    String avatarPublicId;
    @Column(columnDefinition = "MEDIUMTEXT",name = "refresh_token")
    String refreshToken;
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Rating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFavoriteMovie> userFavoriteMovies = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserList> userLists = new HashSet<>();

}
