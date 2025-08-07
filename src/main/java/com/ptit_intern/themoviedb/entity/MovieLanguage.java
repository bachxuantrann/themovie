package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieLanguageId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@IdClass(MovieLanguageId.class)
public class MovieLanguage {
    @Id
    @Column(name = "movie_id")
    Long movieId;
    @Id
    @Column(name = "language_code")
    String languageCode;
}
