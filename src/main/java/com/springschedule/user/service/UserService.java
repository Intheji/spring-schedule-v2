package com.springschedule.user.service;

import com.springschedule.user.dto.CreateUserRequest;
import com.springschedule.user.dto.CreateUserResponse;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
