package com.ptit_intern.themoviedb.dto.response;

import com.ptit_intern.themoviedb.util.enums.RoleEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private InfoResponse infoResponse;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoResponse {
        private Long id;
        private String username;
        private RoleEnum role;
        private String fullName;
        private String description;
        private String avatarUrl;

    }
}
