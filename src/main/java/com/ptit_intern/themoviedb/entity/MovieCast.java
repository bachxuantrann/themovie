package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieGenreId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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
    Long movieId;
    @Id
    Long personId;
    @Id
    String job;
    String characterName;
    Integer orderIndex;

}
