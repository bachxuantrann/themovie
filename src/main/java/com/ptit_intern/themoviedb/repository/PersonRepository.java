package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Person;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository {
    Person getReferenceById(@NotNull(message = "Person ID is required") @Min(value = 1, message = "Person ID must be positive") Long personId);

    boolean existsById(Long personId);
}
