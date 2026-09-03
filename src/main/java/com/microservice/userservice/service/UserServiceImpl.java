package com.microservice.userservice.service;

import com.microservice.userservice.dto.UserProfileResponse;
import com.microservice.userservice.dto.UserUpdateRequest;
import com.microservice.userservice.entity.User;
import com.microservice.userservice.enums.UserStatus;
import com.microservice.userservice.exception.UserNotFoundException;
import com.microservice.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByUsername(String username) {
        User user = fetchUserByUsername(username);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String username, UserUpdateRequest request) {
        User user = fetchUserByUsername(username);

        // Update fields if they are provided
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.profilePictureUrl() != null) user.setProfilePictureUrl(request.profilePictureUrl());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deactivateAccount(String username) {
        User user = fetchUserByUsername(username);
        // Instead of deleting the record, we soft-delete / deactivate it
        user.setStatus(UserStatus.INACTIVE); // Or DELETED based on your exact business rule
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> searchUsers(String keyword, Pageable pageable) {
        Page<User> userPage = userRepository.searchUsersByKeyword(keyword, pageable);
        // Spring Data Page seamlessly maps our Entity page to a DTO page!
        return userPage.map(this::mapToResponse);
    }

    // Helper method to DRY (Don't Repeat Yourself) the code
    private User fetchUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    // Helper method to map Entity to Record DTO
    private UserProfileResponse mapToResponse(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getProfilePictureUrl(),
                user.getStatus(),
                user.getRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}