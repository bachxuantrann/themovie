package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieGenreId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_casts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@IdClass(MovieGenreId.class)
public class MovieCast {
    @Id
    @Column(name = "movie_id")
    Long movieId;
    @Id
    @Column(name = "person_id")
    Long personId;
    @Id
    String job;
    @Column(name = "character_name")
    String characterName;
    @Column(name = "order_index")
    Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id",insertable = false,updatable = false)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id",insertable = false,updatable = false)
    private Person person;
}
