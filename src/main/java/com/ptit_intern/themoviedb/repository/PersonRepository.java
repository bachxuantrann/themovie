package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Person;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person getReferenceById(@NotNull(message = "Person ID is required") @Min(value = 1, message = "Person ID must be positive") Long personId);

    boolean existsById(Long personId);

    boolean existsByName(String name);

    @Query("""
    SELECT p FROM Person p
    WHERE 
        (:keyword IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%'))
    AND (
        :career IS NULL
        OR (LOWER(:career) = 'casting' AND LOWER(TRIM(p.career)) = 'casting')
        OR (LOWER(:career) <> 'casting' AND LOWER(TRIM(p.career)) <> 'casting')
    )
    """)
    Page<Person> searchPersons(
            @Param("keyword") String keyword,
            @Param("career") String career,
            Pageable pageable
    );
    @Query("SELECT p FROM Person p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Person> searchPersonsByName(@Param("keyword") String keyword, Pageable pageable);

}
