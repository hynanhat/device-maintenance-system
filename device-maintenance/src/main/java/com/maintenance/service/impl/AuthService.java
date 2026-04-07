package com.maintenance.service.impl;

import com.maintenance.dto.request.*;
import com.maintenance.dto.response.AuthResponse;
import com.maintenance.dto.response.UserResponse;
import com.maintenance.entity.User;
import com.maintenance.enums.Role;
import com.maintenance.exception.BadRequestException;
import com.maintenance.exception.ResourceNotFoundException;
import com.maintenance.repository.UserRepository;
import com.maintenance.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));

        String accessToken = jwtUtils.generateTokenFromUsername(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng: " + request.getEmail());
        }

        Role role = (request.getRole() != null) ? request.getRole() : Role.ROLE_USER;

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        String newAccessToken = jwtUtils.generateTokenFromUsername(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
