package com.ptit_intern.themoviedb.service;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.UserListDTO;
import com.ptit_intern.themoviedb.dto.request.*;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.IdInvalidExceptions;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    void changePasswordByUser(ChangePasswordRequest changePasswordRequest) throws InvalidExceptions;

    ResultPagination searchAndPagination(int page, int size, String keyword, boolean desc);

    void changePasswordByAdmin(Long id, String password) throws InvalidExceptions;

    void addFavouriteFilms(@Valid AddFavouriteMovieRequest request) throws InvalidExceptions;

    ResultPagination getFavouriteFilms(int page, int size, boolean desc);

    void removeFavouriteFilm(Long id);

    void createListFilm(@Valid CreateListFilmRequest request) throws InvalidExceptions;

    List<UserListDTO> getUserLists(Long id);

    Map<String, Object> getDetailListFilm(Long listId) throws InvalidExceptions;

    void deleteListFilm(Long listId) throws InvalidExceptions;

    void removeFilmFromList(Long listId, Long movieId) throws InvalidExceptions;

    void updateListFilm(@Valid UserListDTO request) throws InvalidExceptions;

    void addMovieToListFilm(Long listId, Long movieId) throws InvalidExceptions;
}
