package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.request.ChangePasswordRequest;
import com.ptit_intern.themoviedb.dto.request.RegisterRequest;
import com.ptit_intern.themoviedb.dto.request.UploadUserRequest;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.IdInvalidExceptions;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    User handleGetUserByUsername(String username);
    void updateRefreshToken(String refreshToken, String username);
    boolean isUserExist(String username);
    User registerUser(RegisterRequest registerRequest);
    void clearUserToken(String username);
    UserDTO updateUser(UploadUserRequest uploadUserRequest) throws IOException;
    String getUsernameById(Long id) throws IdInvalidExceptions;
    UserDTO getDetailUser(Long id);
    void deleteUser(Long id);
    UserDTO createUser(User user) throws InvalidExceptions;
    void changePassword(ChangePasswordRequest changePasswordRequest) throws InvalidExceptions;
}
