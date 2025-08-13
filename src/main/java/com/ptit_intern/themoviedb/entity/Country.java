package com.ptit_intern.themoviedb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country extends BaseEntity<CountryDTO> {
    @NotBlank(message = "country code is required")
    @Column(name = "country_code",unique = true, nullable = false)
    String countryCode;
    @NotBlank(message = "country name is required")
    @Column(name = "name", unique = true, nullable = false)
    String name;
    // Relationships
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCountry> movieCountries = new HashSet<>();
}
