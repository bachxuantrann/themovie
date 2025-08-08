package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country extends BaseEntity{
    @Column(name = "country_code",unique = true, nullable = false)
    String countryCode;
    @Column(name = "name", unique = true, nullable = false)
    String name;
    // Relationships
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCountry> movieCountries = new HashSet<>();
}
