package com.microservice.userservice.service;


import com.microservice.userservice.dto.UserProfileResponse;
import com.microservice.userservice.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserProfileResponse getProfileByUsername(String username);
    UserProfileResponse updateProfile(String username, UserUpdateRequest request);
    void deactivateAccount(String username);
    Page<UserProfileResponse> searchUsers(String keyword, Pageable pageable);
}