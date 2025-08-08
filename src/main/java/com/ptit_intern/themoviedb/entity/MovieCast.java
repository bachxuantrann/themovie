package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieGenreId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_casts",uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "person_id", "job"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieCast extends BaseEntity {
    @Column(name = "movie_id",nullable = false)
    Long movieId;
    @Column(name = "person_id",nullable = false)
    Long personId;
    @Column(name = "job", length = 100, nullable = false)
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
