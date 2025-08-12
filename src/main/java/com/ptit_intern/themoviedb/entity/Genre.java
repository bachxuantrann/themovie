package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.CountryDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre extends BaseEntity<CountryDTO> {
    @Column(length = 100, unique = true, nullable = false)
    String name;

    //    Relationships
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieGenre> movieGenres = new HashSet<>();

}
