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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "Endpoints for managing user accounts and profiles")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Retrieves authenticated user profile information.")
    public ResponseEntity<ApiResponse<AuthDataDto>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserDto userDto = userService.getProfile(currentUser);
        AuthDataDto data = AuthDataDto.builder().user(userDto).build();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/me")
    @Operation(summary = "Update User Profile", description = "Updates user full name or avatar URL.")
    public ResponseEntity<ApiResponse<AuthDataDto>> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        UserDto userDto = userService.updateProfile(currentUser, request);
        AuthDataDto data = AuthDataDto.builder().user(userDto).build();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
