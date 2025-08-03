package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.request.RegisterRequest;
import com.ptit_intern.themoviedb.entity.User;

public interface UserService {
    User handleGetUserByUsername(String username);
    void updateRefreshToken(String refreshToken, String username);
    boolean isUserExist(String username);
    User registerUser(RegisterRequest registerRequest);
    void clearUserToken(String username);
}
