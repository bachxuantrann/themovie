package com.ptit_intern.themoviedb.compositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieCastId {
    private Long movieId;
    private Long personId;
    private String job;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCastId that = (MovieCastId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(personId, that.personId) &&
                Objects.equals(job, that.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, personId, job);
    }
}
