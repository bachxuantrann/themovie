package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.UserListDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserList extends BaseEntity<UserListDTO> {
    @Column(nullable = false, name = "user_id")
    Long userId;
    @Column(name = "name", nullable = false)
    String name;
    @Column(columnDefinition = "TEXT")
    String description;
    @Column(name = "is_public")
    Boolean isPublic=true;
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "userList", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ListItem> listItems = new HashSet<>();
}
