package com.breakingchains.controller;

import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.AuthDataDto;
import com.breakingchains.dto.UpdateProfileRequest;
import com.breakingchains.dto.UserDto;
import com.breakingchains.model.User;
import com.breakingchains.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthDataDto>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserDto userDto = userService.getProfile(currentUser);
        AuthDataDto data = AuthDataDto.builder().user(userDto).build();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<AuthDataDto>> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        UserDto userDto = userService.updateProfile(currentUser, request);
        AuthDataDto data = AuthDataDto.builder().user(userDto).build();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
