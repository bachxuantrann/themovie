package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.CommentDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.request.ChangePasswordRequest;
import com.ptit_intern.themoviedb.dto.request.UploadUserRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.CommentService;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CommentService commentService;

    //  UserService
    @PutMapping()
    @ApiMessage("update info of user")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServiceImpl.getUsernameById(#request.id)")
    public ResponseEntity<UserDTO> updateUser(@ModelAttribute UploadUserRequest request) throws IOException {
        return ResponseEntity.ok().body(this.userService.updateUser(request));
    }

    @GetMapping("/{id}")
    @ApiMessage("get detail infomation of user")
    public ResponseEntity<UserDTO> getDetailUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.userService.getDetailUser(id));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    @ApiMessage("create a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user) throws InvalidExceptions {
        return ResponseEntity.ok().body(userService.createUser(user));
    }

    @PutMapping("/user-change-password")
    @ApiMessage("change user password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> changePasswordByUser(@RequestBody @Valid ChangePasswordRequest request) throws InvalidExceptions {
        this.userService.changePasswordByUser(request);
        return ResponseEntity.ok().body("Password changed");
    }

    @GetMapping
    @ApiMessage("seach and pagination list user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPagination> searchAndPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "true") boolean desc
    ) {
        return ResponseEntity.ok().body(this.userService.searchAndPagination(page, size, keyword, desc));
    }

    @PutMapping("/admin-change-password/{id}")
    @ApiMessage("admin change password of user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changePasswordByAdmin(@PathVariable Long id, @RequestParam String password) throws InvalidExceptions {
        this.userService.changePasswordByAdmin(id, password);
        return ResponseEntity.ok().body("Password changed");
    }

    //    Comment Service
    @PostMapping("/comments")
    @ApiMessage("user comment film")
    public ResponseEntity<CommentDTO> userComment(@RequestBody @Valid CommentDTO commentDTO) throws InvalidExceptions {
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @GetMapping("/comments/{id}")
    @ApiMessage("get comment")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }

    @PutMapping("/comments/{id}")
    @ApiMessage("update comment")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestParam String content) throws InvalidExceptions {
        return ResponseEntity.ok(commentService.updateComment(id, content));
    }

    @DeleteMapping("/comments/{id}")
    @ApiMessage("delete comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) throws InvalidExceptions {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
