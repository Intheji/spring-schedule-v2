package com.springschedule.auth.controller;

import com.springschedule.auth.dto.LoginRequest;
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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        // 이메일로 조회
        User user = userRepository.findByEmail(request.getEmail());

        // 이메일이 존재하지 않으면 로그인 실패
        if (user == null) {
            throw new IllegalArgumentException("님 이메일이 없음");
        }

        // 비밀번호가 일치하지 않으면 로그인 실패
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("님 비밀번호 틀림");
        }

        // 로그인 성공하면 세션에 사용자 식별자 저장
        session.setAttribute("loginUserId", user.getId());

        return ResponseEntity.ok().build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    // 로그인 여부 테스트
    @GetMapping("/test")
    public ResponseEntity<Void> test(
            @SessionAttribute(name = "loginUserId", required = false)
            Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new UnauthorizedException("님 로그인 해 주세요");
        }
        return ResponseEntity.ok().build();
    }
}
