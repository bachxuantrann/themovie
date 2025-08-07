package com.ptit_intern.themoviedb.entity;

import com.ptit_intern.themoviedb.compositeKey.MovieCompanyId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@IdClass(MovieCompanyId.class)
public class MovieCompany {
    @Id
    @Column(name = "movie_id")
    Long movieId;
    @Id
    @Column(name = "company_id")
    Long companyId;
}
