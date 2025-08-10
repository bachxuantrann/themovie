package com.ptit_intern.themoviedb.util;

import com.ptit_intern.themoviedb.dto.response.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    public SecurityUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${themoviedb.jwt.base64-secret}")
    private String jwtKey;
    @Value("${themoviedb.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${themoviedb.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public String createAccessToken(Authentication authentication, AuthResponse.InfoResponse dto) {
        try {
            System.out.println("=== DEBUG CREATE ACCESS TOKEN ===");
            System.out.println("Authentication: " + authentication);
            System.out.println("DTO: " + dto);

            Instant now = Instant.now();
            Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

            // Tạo nested object cho user claim với role
            JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                    .issuedAt(now)
                    .expiresAt(validity)
                    .subject(authentication.getName());

            // Thêm user info với null safety - theo structure AuthResponse thực tế
            java.util.Map<String, Object> userClaim = new java.util.HashMap<>();
            userClaim.put("id", dto != null ? dto.getId() : null);
            userClaim.put("username", dto != null ? dto.getUsername() : authentication.getName());
            userClaim.put("fullName", dto != null ? dto.getFullName() : "");
            userClaim.put("description", dto != null ? dto.getDescription() : "");
            userClaim.put("role", dto != null && dto.getRole() != null ? dto.getRole().name() : "USER");
            userClaim.put("avatarUrl", dto != null && dto.getAvatarUrl() != null ? dto.getAvatarUrl() : "");

            System.out.println("User claim: " + userClaim);

            claimsBuilder.claim("user", userClaim);

            JwtClaimsSet claims = claimsBuilder.build();
            JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

            String token = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
            System.out.println("Token created successfully");
            return token;
        } catch (Exception e) {
            System.err.println("Error creating access token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    public String createRefreshToken(String username, AuthResponse dto) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username);

        // Thêm user info với null safety cho refresh token - theo structure thực tế
        AuthResponse.InfoResponse infoResponse = dto != null ? dto.getInfoResponse() : null;
        java.util.Map<String, Object> userClaim = new java.util.HashMap<>();
        userClaim.put("id", infoResponse != null ? infoResponse.getId() : null);
        userClaim.put("username", infoResponse != null ? infoResponse.getUsername() : username);
        userClaim.put("fullName", infoResponse != null ? infoResponse.getFullName() : "");
        userClaim.put("description", infoResponse != null ? infoResponse.getDescription() : "");
        userClaim.put("role", infoResponse != null && infoResponse.getRole() != null ? infoResponse.getRole().name() : "USER");
        userClaim.put("avatarUrl", infoResponse != null && infoResponse.getAvatarUrl() != null ? infoResponse.getAvatarUrl() : "");

        claimsBuilder.claim("user", userClaim);

        JwtClaimsSet claims = claimsBuilder.build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    public static Optional<String> getCurrentUserLogin(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));

    }
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }
    public Jwt decodeJwt(String token){
        return this.jwtDecoder.decode(token);
    }
}
