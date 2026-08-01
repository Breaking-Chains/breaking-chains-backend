package com.breakingchains.dto;

import com.breakingchains.model.AuthProvider;
import com.breakingchains.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;
    private String email;
    private String fullName;
    private String username;
    private String avatarUrl;
    private String bio;
    private AuthProvider authProvider;
    private String createdAt;
    private String updatedAt;

    public static UserDto fromEntity(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String createdStr = user.getCreatedAt() != null
                ? user.getCreatedAt().atZone(ZoneOffset.UTC).format(formatter)
                : null;
        String updatedStr = user.getUpdatedAt() != null
                ? user.getUpdatedAt().atZone(ZoneOffset.UTC).format(formatter)
                : null;

        return UserDto.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .authProvider(user.getAuthProvider())
                .createdAt(createdStr)
                .updatedAt(updatedStr)
                .build();
    }
}
