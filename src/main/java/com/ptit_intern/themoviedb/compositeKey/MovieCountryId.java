package com.ptit_intern.themoviedb.compositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCountryId implements Serializable {
    private Long movieId;
    private String countryCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCountryId that = (MovieCountryId) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(countryCode, that.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, countryCode);
    }
}
