package com.maintenance.dto.response;

import com.maintenance.enums.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
