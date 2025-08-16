package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_genres", uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "genre_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieGenre extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
