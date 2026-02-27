package com.springschedule.auth.service;

import com.springschedule.auth.dto.LoginRequest;
import com.springschedule.common.exception.UnauthorizedException;
import com.springschedule.config.PasswordEncoder;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String LOGIN_USER_ID = "loginUserId";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        session.setAttribute(LOGIN_USER_ID, user.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
