package com.springschedule.user.service;

import com.springschedule.user.dto.*;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse save(CreateUserRequest request) {

        User user = new User(
                request.getUserName(),
                request.getEmail()
        );

        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findOne(Long userId) {
        User user = getUserOrThrow(userId);
        return toResponse(user);
    }

    @Transactional
    public UserResponse update(Long userId, UpdateUserRequest request) {
        User user = getUserOrThrow(userId);
        user.update(request.getUserName(), request.getEmail());
        return toResponse(user);
    }

    @Transactional
    public void delete(Long userId) {
        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 존재하지 않는데요?")
        );
    }
}
