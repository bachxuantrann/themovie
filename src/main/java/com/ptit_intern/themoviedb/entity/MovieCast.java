package com.ptit_intern.themoviedb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movie_casts", uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "person_id", "job"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MovieCast extends BaseEntity {
    @Column(name = "job", length = 100, nullable = false)
    String job;
    @Column(name = "character_name")
    String characterName;
    @Column(name = "order_index")
    Integer orderIndex;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private Person person;
}
