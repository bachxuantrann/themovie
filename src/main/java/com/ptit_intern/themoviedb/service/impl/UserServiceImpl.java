package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.request.RegisterRequest;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.repository.UserRepository;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void updateRefreshToken(String refreshToken, String username) {
        User currentUser = this.userRepository.findByUsername(username);
        if(currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }
    public boolean isUserExist(String username){
        return this.userRepository.findByUsername(username.trim()) instanceof User ? true : false;
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(RoleEnum.ADMIN);
        return this.userRepository.save(user);
    }

    @Override
    public void clearUserToken(String username) {
        User user = this.userRepository.findByUsername(username);
        if(user != null) {
            user.setRefreshToken(null);
            this.userRepository.save(user);
        }
    }
}
