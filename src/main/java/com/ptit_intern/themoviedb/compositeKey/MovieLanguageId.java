package com.ptit_intern.themoviedb.compositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieLanguageId implements Serializable {
    private Long movieId;
    private String languageCode;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieLanguageId that = (MovieLanguageId) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(languageCode, that.languageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, languageCode);
    }
}
