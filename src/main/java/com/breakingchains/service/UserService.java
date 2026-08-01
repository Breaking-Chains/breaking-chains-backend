package com.breakingchains.service;

import com.breakingchains.dto.UpdateProfileRequest;
import com.breakingchains.dto.UserDto;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.User;
import com.breakingchains.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getProfile(User currentUser) {
        return UserDto.fromEntity(currentUser);
    }

    @Transactional
    public UserDto updateProfile(User currentUser, UpdateProfileRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String newUsername = request.getUsername().trim();
            if (!newUsername.equalsIgnoreCase(currentUser.getUsername())) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw AppException.userExists("Username already taken");
                }
                currentUser.setUsername(newUsername);
            }
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            currentUser.setFullName(request.getFullName().trim());
        }

        if (request.getBio() != null) {
            currentUser.setBio(request.getBio().trim());
        }

        if (request.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(request.getAvatarUrl().trim());
        }

        User updatedUser = userRepository.save(currentUser);
        return UserDto.fromEntity(updatedUser);
    }
}
