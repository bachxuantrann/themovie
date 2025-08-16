package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name,Long id);
    @Query("SELECT c FROM Company c " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Company> searchCompanies(@Param("keyword") String keyword, Pageable pageable);
}
