package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.LanguageDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Column(name = "language_code", unique = true, nullable = false)
    @NotBlank(message = "language code is required")
    String languageCode;
    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "language name is required")
    String name;
    // Relationships
    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieLanguage> movieLanguages = new HashSet<>();

}
