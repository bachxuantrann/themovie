package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_countries", uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "country_id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieCountry extends BaseEntity {
    @Column(name = "movie_id", nullable = false)
    Long movieId;
    @Column(name = "country_id", nullable = false)
    private String countryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private Country country;

}
