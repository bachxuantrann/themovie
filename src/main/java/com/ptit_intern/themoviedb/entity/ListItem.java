package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "list_items",uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "movie_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListItem extends BaseEntity{
    @Column(name = "list_id", nullable = false)
    Long listId;
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
//   Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", insertable = false, updatable = false)
    private UserList userList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;
}
