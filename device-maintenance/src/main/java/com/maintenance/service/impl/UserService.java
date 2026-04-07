package com.maintenance.service.impl;

import com.maintenance.dto.request.ChangePasswordRequest;
import com.maintenance.dto.request.RegisterRequest;
import com.maintenance.dto.request.UpdateProfileRequest;
import com.maintenance.dto.response.UserResponse;
import com.maintenance.entity.User;
import com.maintenance.enums.Role;
import com.maintenance.exception.BadRequestException;
import com.maintenance.exception.ResourceNotFoundException;
import com.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User không tìm thấy"));
    }

    public UserResponse getProfile() {
        return toResponse(getCurrentUser());
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email đã được sử dụng");
            }
            user.setEmail(request.getEmail());
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận không khớp");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Transactional
    public UserResponse createUserByAdmin(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }
        Role role = request.getRole() != null ? request.getRole() : Role.ROLE_USER;
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .enabled(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setEnabled(!user.isEnabled());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    public List<UserResponse> getTechnicians() {
        return userRepository.findAllTechnicians().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setAvatarUrl(avatarUrl);
        return toResponse(userRepository.save(user));
    }

    public UserResponse toResponse(User user) {
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
