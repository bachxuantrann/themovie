package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.CommentDTO;
import com.ptit_intern.themoviedb.entity.Comment;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.CommentRepository;
import com.ptit_intern.themoviedb.repository.UserRepository;
import com.ptit_intern.themoviedb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setMovieId(commentDTO.getMovieId());
        Long userId = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        comment.setUserId(userId);
        comment.setContent(commentDTO.getContent());
        return this.commentRepository.save(comment).toDTO(CommentDTO.class);
    }

    @Override
    public CommentDTO getComment(Long id) {
       return this.commentRepository.findById(id).get().toDTO(CommentDTO.class);
    }

    @Override
    public CommentDTO updateComment(Long id, String content) throws InvalidExceptions {
        if (!commentRepository.existsById(id)) {
            throw new InvalidExceptions("Comment is not existed");
        }
        Comment comment = commentRepository.findById(id).get();
        comment.setContent(content);
        return this.commentRepository.save(comment).toDTO(CommentDTO.class);
    }

    @Override
    public void deleteComment(Long id) throws InvalidExceptions {
        if (!commentRepository.existsById(id)) {
            throw new InvalidExceptions("Comment is not existed");
        }
        commentRepository.deleteById(id);
    }
}
