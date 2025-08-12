package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language extends BaseEntity<LanguageDTO> {
    @Column(name = "language_code",unique = true, nullable = false)
    String languageCode;
    @Column(name = "name", unique = true, nullable = false)
    String name;
    // Relationships
    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieLanguage> movieLanguages = new HashSet<>();

}
