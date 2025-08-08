package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieGenreId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@IdClass(MovieGenreId.class)
public class MovieGenre {
    @Id
    @Column(name = "movie_id")
    Long movieId;
    @Id
    @Column(name = "genre_id")
    Long genreId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id",insertable = false,updatable = false)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id",insertable = false,updatable = false)
    private Genre genre;
}
