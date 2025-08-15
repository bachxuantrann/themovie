package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.CommentDTO;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    CommentDTO getComment(Long id);
    CommentDTO updateComment(Long id, String content) throws InvalidExceptions;
    void deleteComment(Long id) throws InvalidExceptions;
}
