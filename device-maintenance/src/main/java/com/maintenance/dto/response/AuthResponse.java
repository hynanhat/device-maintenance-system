package com.maintenance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String avatarUrl;
}
