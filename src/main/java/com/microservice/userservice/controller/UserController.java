package com.microservice.userservice.controller;

import com.microservice.userservice.dto.UserProfileResponse;
import com.microservice.userservice.dto.UserUpdateRequest;
import com.microservice.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Get my own profile (Extracts username from the JWT token automatically!)
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        UserProfileResponse response = userService.getProfileByUsername(principal.getName());
        return ResponseEntity.ok(response);
    }

    // 2. Update my own profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Principal principal,
            @Valid @RequestBody UserUpdateRequest request) {

        UserProfileResponse response = userService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    // 3. Deactivate my own account
    @DeleteMapping("/me")
    public ResponseEntity<String> deactivateMyAccount(Principal principal) {
        userService.deactivateAccount(principal.getName());
        return ResponseEntity.ok("Account deactivated successfully");
    }

    // 4. Search users (Pagination is injected automatically via URL parameters like ?page=0&size=10)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Page<UserProfileResponse>> searchUsers(
            @RequestParam("keyword") String keyword,
            Pageable pageable) {

        Page<UserProfileResponse> response = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}