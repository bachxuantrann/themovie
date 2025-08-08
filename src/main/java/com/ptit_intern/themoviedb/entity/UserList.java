package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserList extends BaseEntity {
    @Column(nullable = false, name = "user_id")
    Long userId;
    @Column(name = "name", nullable = false)
    String name;
    @Column(columnDefinition = "TEXT")
    String description;
    @Column(name = "is_public")
    Boolean isPublic=false;
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "userList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ListItem> listItems = new HashSet<>();
}
