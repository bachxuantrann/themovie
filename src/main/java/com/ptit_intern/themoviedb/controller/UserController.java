package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.request.UploadUserRequest;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
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

    @PutMapping
    @ApiMessage("update info of user")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServiceImpl.getUsernameById(#id)")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UploadUserRequest uploadUserRequest
            , @RequestParam(required = false) MultipartFile avatar, @RequestParam(required = false) Boolean removeAvatar
    ) throws IOException {
        return ResponseEntity.ok().body(this.userService.updateUser(uploadUserRequest, avatar, removeAvatar));
    }
}
