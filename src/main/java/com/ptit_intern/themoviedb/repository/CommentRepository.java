package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
