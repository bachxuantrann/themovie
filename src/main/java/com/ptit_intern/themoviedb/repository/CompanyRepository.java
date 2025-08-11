package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
