package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company extends BaseEntity<CompanyDTO> {
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "logo_path",columnDefinition = "TEXT")
    String logoPath;
    @Column(name = "logo_public_id",columnDefinition = "TEXT")
    String logoPublicId;
    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MovieCompany> movieCompanies = new HashSet<>();

}
