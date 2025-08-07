package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserList extends BaseEntity {
    @Column(nullable = false, name = "user_id")
    Long userId;
    String name;
    @Column(columnDefinition = "TEXT")
    String description;
    @Column(name = "is_public")
    Boolean isPublic=false;

}
