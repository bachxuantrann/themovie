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
@Table(name = "list_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListItem extends BaseEntity{
    @Column(name = "list_id", nullable = false)
    Long listId;
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
}
