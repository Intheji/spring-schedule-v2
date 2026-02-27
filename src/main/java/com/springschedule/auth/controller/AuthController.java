package com.springschedule.auth.controller;

import com.springschedule.auth.dto.LoginRequest;
import com.springschedule.auth.service.AuthService;
import com.springschedule.common.exception.UnauthorizedException;
import com.springschedule.config.PasswordEncoder;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        authService.login(request, session);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

}
