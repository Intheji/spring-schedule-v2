package com.springschedule.user.service;

import com.springschedule.user.dto.CreateUserRequest;
import com.springschedule.user.dto.CreateUserResponse;
import com.springschedule.user.dto.GetUserResponse;
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

    @Transactional
    public CreateUserResponse save(CreateUserRequest request) {

        User user = new User(
                request.getUserName(),
                request.getEmail()
        );

        User saved = userRepository.save(user);

        return new CreateUserResponse(
                saved.getId(),
                saved.getUserName(),
                saved.getEmail(),
                saved.getCreatedAt(),
                saved.getModifiedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<GetUserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GetUserResponse findOne(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 존재하지 않는데요?")
        );
        return toResponse(user);
    }

    private GetUserResponse toResponse(User user) {
        return new GetUserResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
