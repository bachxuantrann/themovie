package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.request.ChangePasswordRequest;
import com.ptit_intern.themoviedb.dto.request.UploadUserRequest;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping()
    @ApiMessage("update info of user")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServiceImpl.getUsernameById(#request.id)")
    public ResponseEntity<UserDTO> updateUser(@ModelAttribute UploadUserRequest request) throws IOException {
        return ResponseEntity.ok().body(this.userService.updateUser(request));
    }

    @GetMapping("/{id}")
    @ApiMessage("get detail infomation of user")
    public ResponseEntity<UserDTO> getDetailUser(@PathVariable Long id){
        return ResponseEntity.ok().body(this.userService.getDetailUser(id));
    }
    @DeleteMapping("/{id}")
    @ApiMessage("delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        this.userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping()
    @ApiMessage("create a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user) throws InvalidExceptions {
        return ResponseEntity.ok().body(userService.createUser(user));
    }
    @PutMapping("/change-password")
    @ApiMessage("change user password")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServiceImpl.getUsernameById(#request.id)")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) throws InvalidExceptions {
        this.userService.changePassword(request);
        return ResponseEntity.ok().body("Password changed");
    }
}
