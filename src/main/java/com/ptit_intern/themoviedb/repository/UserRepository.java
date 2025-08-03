package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
