package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.UserDTO;
import com.ptit_intern.themoviedb.dto.request.RegisterRequest;
import com.ptit_intern.themoviedb.dto.request.UploadUserRequest;
import com.ptit_intern.themoviedb.entity.User;
import com.ptit_intern.themoviedb.exception.IdInvalidExceptions;
import com.ptit_intern.themoviedb.repository.UserRepository;
import com.ptit_intern.themoviedb.service.UserService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service("userServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

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
        user.setRole(RoleEnum.USER);
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

    @Override
    public UserDTO updateUser(UploadUserRequest uploadUserRequest) throws IOException {
        User user = userRepository.findById(uploadUserRequest.getId()).orElseThrow(()-> new IllegalStateException("User Not Found"));
        MultipartFile avatar = uploadUserRequest.getAvatar();
        Boolean removeAvatar = uploadUserRequest.getRemoveAvatar();
        String fullName = uploadUserRequest.getFullName() == null ? "" : uploadUserRequest.getFullName().trim();
        String email = uploadUserRequest.getEmail() == null ? "" : uploadUserRequest.getEmail().trim();
        String description = uploadUserRequest.getDescription() == null ? "" : uploadUserRequest.getDescription().trim();
        if ( fullName != "") user.setFullName(fullName);
        if ( email != "") user.setEmail(email);
        if ( description != "") user.setDescription(description);
        boolean hasNewAvatar = avatar != null && !avatar.isEmpty();
        boolean wantRemoveAvatar = Boolean.TRUE.equals(removeAvatar);
        if (hasNewAvatar) {
            UploadOptions options = new UploadOptions();
            options.setFolder("users");
            options.setTags(List.of("user","avatar"));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(avatar, options);
            String newUrl = uploadRes.secureUrl();
            String newPublicId = uploadRes.publicId();
            String oldUrl = user.getAvatarUrl();
            String oldPublicId = user.getAvatarPublicId();
            if(newUrl != null && !newUrl.isEmpty()){
                user.setAvatarUrl(newUrl);
            }
            if (newPublicId != null && !newPublicId.isEmpty()){
                user.setAvatarPublicId(newPublicId);
            }
            try{
                if (oldPublicId != null && !oldPublicId.isBlank()){
                    cloudinaryService.deleteImageByPublicId(oldPublicId);
                } else if (oldUrl!=null && !oldUrl.isBlank()){
                    cloudinaryService.deleteImageByUrl(oldUrl);
                }
            } catch (Exception ex){
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
}
