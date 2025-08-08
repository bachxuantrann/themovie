package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_companies", uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "company_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieCompany extends BaseEntity {
    @Column(name = "movie_id", nullable = false)
    Long movieId;
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;
}
