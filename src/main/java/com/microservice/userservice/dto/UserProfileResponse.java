package com.microservice.userservice.dto;

import com.microservice.userservice.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.Set;

public record UserProfileResponse(
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePictureUrl,
        UserStatus status,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}