package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.MovieDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.UserListDTO;
import com.ptit_intern.themoviedb.dto.request.*;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.*;
import com.ptit_intern.themoviedb.exception.IdInvalidExceptions;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.*;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("userServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserFavouriteMovieRepository userFavoriteMovieRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final MovieRepository movieRepository;
    private final UserFavouriteMovieRepository userFavouriteMovieRepository;
    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private String avatarDefault = "https://res.cloudinary.com/dpioj21ib/image/upload/v1754814358/default-avatar-icon-of-social-media-user-vector_vqtt1m.jpg";

    public record MovieList(Long id, String title, Long budget, Long revenue, BigDecimal voteAverage,
                            Integer voteCount) {
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void updateRefreshToken(String refreshToken, String username) {
        User currentUser = this.userRepository.findByUsername(username);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }

    public boolean isUserExist(String username) {
        return this.userRepository.findByUsername(username.trim()) instanceof User ? true : false;
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(RoleEnum.USER);
        return this.userRepository.save(user);
    }

    @Override
    public void clearUserToken(String username) {
        User user = this.userRepository.findByUsername(username);
        if (user != null) {
            user.setRefreshToken(null);
            this.userRepository.save(user);
        }
    }

    @Override
    public UserDTO updateUser(UploadUserRequest uploadUserRequest) throws IOException {
        User user = userRepository.findById(uploadUserRequest.getId()).orElseThrow(() -> new IllegalStateException("User Not Found"));
        MultipartFile avatar = uploadUserRequest.getAvatar();
        Boolean removeAvatar = uploadUserRequest.getRemoveAvatar();
        String fullName = uploadUserRequest.getFullName() == null ? "" : uploadUserRequest.getFullName().trim();
        String email = uploadUserRequest.getEmail() == null ? "" : uploadUserRequest.getEmail().trim();
        String description = uploadUserRequest.getDescription() == null ? "" : uploadUserRequest.getDescription().trim();
        if (fullName != "") user.setFullName(fullName);
        if (email != "") user.setEmail(email);
        if (description != "") user.setDescription(description);
        boolean hasNewAvatar = avatar != null && !avatar.isEmpty();
        boolean wantRemoveAvatar = Boolean.TRUE.equals(removeAvatar);
        if (hasNewAvatar) {
            UploadOptions options = new UploadOptions();
            options.setFolder("users");
            options.setTags(List.of("user", "avatar"));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(avatar, options);
            String newUrl = uploadRes.secureUrl();
            String newPublicId = uploadRes.publicId();
            String oldUrl = user.getAvatarUrl();
            String oldPublicId = user.getAvatarPublicId();
            if (newUrl != null && !newUrl.isEmpty()) {
                user.setAvatarUrl(newUrl);
            }
            if (newPublicId != null && !newPublicId.isEmpty()) {
                user.setAvatarPublicId(newPublicId);
            }
            try {
                if (oldPublicId != null && !oldPublicId.isBlank()) {
                    cloudinaryService.deleteImageByPublicId(oldPublicId);
                } else if (oldUrl != null && !oldUrl.isBlank()) {
                    cloudinaryService.deleteImageByUrl(oldUrl);
                }
            } catch (Exception ex) {
                log.warn("Xóa ảnh cũ trên Cloudinary thất bại (không rollback): userId={}, oldPublicId={}, oldUrl={}, error={}",
                        user.getId(), oldPublicId, oldUrl, ex.getMessage());
            }
        } else if (wantRemoveAvatar) {
            String publicId = user.getAvatarPublicId();
            String url = user.getAvatarUrl();
            if (publicId != null && !publicId.isBlank()) {
                cloudinaryService.deleteImageByPublicId(publicId);
            } else if (url != null && !url.isBlank()) {
                cloudinaryService.deleteImageByUrl(url);
            }
            user.setAvatarPublicId(null);
            user.setAvatarUrl(null);
        }
        this.userRepository.save(user);
        return user.toDTO(UserDTO.class);
    }

    @Override
    public String getUsernameById(Long id) throws IdInvalidExceptions {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidExceptions("user not found"))
                .getUsername();
    }

    @Override
    public UserDTO getDetailUser(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        ).toDTO(UserDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        String avatarUrl = user.getAvatarUrl();
        String publicId = user.getAvatarPublicId();
        try {
            if (publicId != null && !publicId.isBlank()) {
                cloudinaryService.deleteImageByPublicId(publicId);
            } else if (avatarUrl != null && !avatarUrl.isBlank()) {
                cloudinaryService.deleteImageByUrl(avatarUrl);
            }
        } catch (Exception ex) {
            log.warn("Xóa ảnh  trên Cloudinary thất bại (không rollback): userId={}, oldPublicId={}, oldUrl={}, error={}",
                    user.getId(), publicId, avatarUrl, ex.getMessage());
        }
        this.userRepository.delete(user);
    }

    @Override
    public UserDTO createUser(User user) throws InvalidExceptions {
        User newUser = new User();
        if (handleGetUserByUsername(user.getUsername()) != null) {
            throw new InvalidExceptions("User is existed");
        }
        newUser.setUsername(user.getUsername().trim());
        newUser.setPassword(user.getPassword().trim());
        newUser.setRole(user.getRole());
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            newUser.setEmail(user.getEmail().trim());
        }
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            newUser.setFullName(user.getFullName().trim());
        }
        if (user.getDescription() != null && !user.getDescription().isEmpty()) {
            newUser.setDescription(user.getDescription().trim());
        }
        newUser.setAvatarUrl(avatarDefault);
        newUser.setAvatarPublicId(cloudinaryService.extractPublicIdFromUrl(newUser.getAvatarUrl()));
        return this.userRepository.save(newUser).toDTO(UserDTO.class);
    }

    @Override
    public void changePasswordByUser(ChangePasswordRequest changePasswordRequest) throws InvalidExceptions {
        User user = this.userRepository.findById(changePasswordRequest.getId()).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword().trim())) {
            throw new InvalidExceptions("Old password does not match");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword().trim()));
        this.userRepository.save(user);
    }

    @Override
    public ResultPagination searchAndPagination(int page, int size, String keyword, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<User> users = this.userRepository.searchUsers(keyword, pageable);
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS = users.getContent().stream().map(user -> user.toDTO(UserDTO.class)).collect(Collectors.toList());
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(userDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(users.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(users.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    @Override
    public void changePasswordByAdmin(Long id, String password) throws InvalidExceptions {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(password.trim()));
        }
        this.userRepository.save(user);
    }

    @Override
    public void addFavouriteFilms(AddFavouriteMovieRequest request) throws InvalidExceptions {
        if (userRepository.existsById(request.getUserId()) && movieRepository.existsById(request.getMovieId())) {
            UserFavoriteMovie userFavoriteMovie = new UserFavoriteMovie();
            userFavoriteMovie.setUser(userRepository.findById(request.getUserId()).get());
            userFavoriteMovie.setMovie(movieRepository.findById(request.getMovieId()).get());
            userFavouriteMovieRepository.save(userFavoriteMovie);
            return;
        }
        throw new InvalidExceptions("userId or movieId is not existed");
    }

    @Override
    @Transactional
    public ResultPagination getFavouriteFilms(int page, int size, boolean desc) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(userName);
        Long userId = user.getId();
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Long> movieIdsPage = userFavouriteMovieRepository.findFavoriteMovieIdsByUserId(userId, pageable);
        List<Movie> movies = movieRepository.findAllById(movieIdsPage.getContent());
        List<MovieDTO> movieDTOS = movieRepository.findAllById(movieIdsPage.getContent())
                .stream().map(movie -> movie.toDTO(MovieDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(movieDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(movieIdsPage.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(movieIdsPage.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    @Override
    @Transactional
    public void removeFavouriteFilm(Long movieId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(userName);
        Long userId = user.getId();
        if (userRepository.existsById(userId) && movieRepository.existsById(movieId)) {
            userFavoriteMovieRepository.deleteByUserIdAndMovieId(userId, movieId);
            return;
        }
        throw new RuntimeException("user or movie is not existed");
    }

    @Override
    public void createListFilm(CreateListFilmRequest request) throws InvalidExceptions {
        if (userListRepository.existsByUserIdAndName(request.getUserId(), request.getName())) {
            throw new InvalidExceptions("Name of list is existed");
        }
        UserList userList = new UserList();
        userList.setUserId(request.getUserId());
        userList.setName(request.getName());
        userList.setDescription(request.getDescription());
        userList.setIsPublic(request.getIsPublic());
        this.userListRepository.save(userList);
    }

    @Override
    public List<UserListDTO> getUserLists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("user not found");
        }
        List<UserListDTO> data = userListRepository.findByUserId(userId).stream().map(userList -> userList.toDTO(UserListDTO.class)).toList();
        Long idOfUserHasList = data.get(0).getUserId();
        if (userId.equals(idOfUserHasList)) {
            return data;
        }
        return data.stream().filter(UserListDTO::getIsPublic).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDetailListFilm(Long listId) throws InvalidExceptions {
        UserListDTO userListDTO = this.userListRepository.findById(listId).orElseThrow(
                () -> new InvalidExceptions("List film of user is not existed")
        ).toDTO(UserListDTO.class);
        Set<Long> movieIds = listItemRepository.findByListId(listId)
                .stream()
                .map(ListItem::getMovieId)
                .collect(Collectors.toSet());
        List<MovieList> movieLists = movieRepository.findAllById(movieIds)
                .stream()
                .map(movie -> new MovieList(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getBudget(),
                        movie.getRevenue(),
                        movie.getVoteAverage(),
                        movie.getVoteCount()
                ))
                .toList();
        Map<String, Object> result = new HashMap<>();
        result.put("movies", movieLists);
        result.put("listFilm", userListDTO);
        return result;
    }

    @Override
    @Transactional
    public void deleteListFilm(Long listId) throws InvalidExceptions {
        if (userListRepository.existsById(listId)) {
            userListRepository.deleteById(listId);
            return;
        }
        throw new InvalidExceptions("list film is not found");
    }

    @Override
    @Transactional
    public void removeFilmFromList(Long listId, Long movieId) throws InvalidExceptions {
        if (userListRepository.existsById(listId) && movieRepository.existsById(movieId)) {
            listItemRepository.deleteByListIdAndMovieId(listId, movieId);
            return;
        }
        throw new InvalidExceptions("list or movie is not existed");
    }

    @Override
    public void updateListFilm(UserListDTO request) throws InvalidExceptions {
        UserList userList = this.userListRepository.findById(request.getId()).orElseThrow(() -> new InvalidExceptions("List film of user is not existed"));
        if (userListRepository.existsByUserIdAndNameAndIdNot(request.getUserId(), request.getName(), userList.getId())) {
            throw new InvalidExceptions("Name list film of user is existed");
        }
        userList.setName(request.getName());
        userList.setDescription(request.getDescription());
        userList.setIsPublic(request.getIsPublic());
        this.userListRepository.save(userList);
    }

    @Override
    public void addMovieToListFilm(Long listId, Long movieId) throws InvalidExceptions {
        if (userListRepository.existsById(listId) && movieRepository.existsById(movieId)) {
            ListItem listItem = new ListItem();
            listItem.setListId(listId);
            listItem.setMovieId(movieId);
            listItemRepository.save(listItem);
            return;
        }
        throw new InvalidExceptions("List or Movie is not existed");
    }
}
