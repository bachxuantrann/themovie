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
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company extends BaseEntity {
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "logo_path",columnDefinition = "TEXT")
    String logoPath;
    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCompany> movieCompanies = new HashSet<>();

}
