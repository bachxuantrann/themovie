package com.ptit_intern.themoviedb.controller;

import com.ptit_intern.themoviedb.dto.request.AuthRequest;
import com.ptit_intern.themoviedb.dto.request.RegisterRequest;
import com.ptit_intern.themoviedb.dto.response.AuthResponse;
import com.ptit_intern.themoviedb.dto.response.RegisterResponse;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.exception.UserExisted;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.util.SecurityUtil;
import com.ptit_intern.themoviedb.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${themoviedb.jwt.refresh-token-validity-in-seconds}")
    private long refeshTokenExpiration;

    @PostMapping("/login")
    @ApiMessage("user login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        AuthResponse authResponse = new AuthResponse();
        User currentUser = this.userService.handleGetUserByUsername(authRequest.getUsername());
        AuthResponse.InfoResponse infoResponse = new AuthResponse.InfoResponse
                (currentUser.getId(), currentUser.getUsername(), currentUser.getRole().name(), currentUser.getFullName(), currentUser.getDescription(), currentUser.getAvatarUrl());
        authResponse.setInfoResponse(infoResponse);
        String accessToken = this.securityUtil.createAccessToken(authentication, authResponse.getInfoResponse());
        authResponse.setAccessToken(accessToken);
        String refreshToken = this.securityUtil.createRefreshToken(authRequest.getUsername(), authResponse);
        this.userService.updateRefreshToken(refreshToken, authRequest.getUsername());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refeshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(authResponse);
    }

    @PostMapping("/register")
    @ApiMessage("register new account for user")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) throws UserExisted {
        if (this.userService.isUserExist(registerRequest.getUsername())) {
            throw new UserExisted("Username already exists");
        }
//      Register User
        User newUser = this.userService.registerUser(registerRequest);
//      Create authentication fake to create access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser.getUsername(), null, List.of(new SimpleGrantedAuthority(newUser.getRole().name()))
        );
        AuthResponse.InfoResponse infoResponse = AuthResponse.InfoResponse.builder()
                .id(newUser.getId()).username(newUser.getUsername()).role(newUser.getRole().name()).build();
//      Create access token
        String accessToken = this.securityUtil.createAccessToken(authentication, infoResponse);
//      Create refresh token
        AuthResponse authResponse = AuthResponse.builder().accessToken(accessToken).infoResponse(infoResponse).build();
        String refreshToken = this.securityUtil.createRefreshToken(registerRequest.getUsername(), authResponse);
//      create Register Response
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterResponse.InfoRegister infoRegister = new RegisterResponse.InfoRegister(newUser.getId(), newUser.getUsername(), newUser.getRole().name());
        registerResponse.setAccessToken(refreshToken);
        registerResponse.setInfoRegister(infoRegister);
        this.userService.updateRefreshToken(refreshToken, registerRequest.getUsername());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refeshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(registerResponse);
    }

    //  Log out account
    @PostMapping("/logout")
    @ApiMessage("user log out")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) throws InvalidExceptions {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidExceptions("Refresh token is missing");
        }
        String username;
        try {
            Jwt decodedToken = securityUtil.decodeJwt(refreshToken);
            username = decodedToken.getSubject();
        } catch (Exception e) {
            throw new InvalidExceptions("Refresh token is invalid");
        }
        this.userService.clearUserToken(username);
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logout successful!");
    }

    //  Fetch account user
    @GetMapping("/account")
    @ApiMessage("fetch account")
    public ResponseEntity<AuthResponse.InfoResponse> getAccount() {
        String username = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByUsername(username);
        AuthResponse.InfoResponse infoResponse = new AuthResponse.InfoResponse();
        if (currentUser != null) {
            infoResponse.setId(currentUser.getId());
            infoResponse.setUsername(currentUser.getUsername());
            infoResponse.setRole(currentUser.getRole().name());
            infoResponse.setAvatarUrl(currentUser.getAvatarUrl());
            infoResponse.setFullName(currentUser.getFullName());
            infoResponse.setDescription(currentUser.getDescription());
        }
        return ResponseEntity.ok().body(infoResponse);
    }

}
