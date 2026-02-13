package com.springschedule.user.service;

import com.springschedule.config.PasswordEncoder;
import com.springschedule.user.dto.*;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserResponse save(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이메일 이미 사용 중..");
        }
        // 비밀번호 해싱
        String encoded = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUserName(),
                request.getEmail(),
                encoded
        );

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // 유저 목록 조회
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // 유저 단건 조회
    @Transactional(readOnly = true)
    public UserResponse findOne(Long userId) {
        User user = getUserOrThrow(userId);
        return toResponse(user);
    }

    // 유저 수정
    @Transactional
    public UserResponse update(Long userId, UpdateUserRequest request) {

        if (request.getUserName() == null && request.getEmail() == null) {
            throw new IllegalArgumentException("수정할 값 없음");
        }
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new IllegalArgumentException("이메일 이미 사용 중..");
        }
        User user = getUserOrThrow(userId);
        String newUserName = (request.getUserName() != null) ? request.getUserName() : user.getUserName();
        String newEmail    = (request.getEmail() != null) ? request.getEmail() : user.getEmail();
        user.update(newUserName, newEmail);
        return toResponse(user);
    }

    // 유저 삭제
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

    // 유저 조회
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 존재하지 않는데요?")
        );
    }
}
