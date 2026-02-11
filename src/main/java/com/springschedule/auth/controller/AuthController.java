package com.springschedule.auth.controller;

import com.springschedule.auth.dto.LoginRequest;
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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        User user = userRepository.findByEmail(request.getEmail());

        // 이메일 없으면 로그인 실패
        if (user == null) {
            throw new IllegalArgumentException("님 이메일이 없음");
        }

        // 비밀번호 틀리면 로그인 실패
        if (!user.getPassword().matches(request.getPassword())) {
        throw new IllegalArgumentException("님 비밀번호 틀림");
        }

        // 로그인 성공
        session.setAttribute("loginUserId", user.getId());

        return ResponseEntity.ok().build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test(
            @SessionAttribute(name = "loginUserId", required = false)
            Long loginUserId
    ) {
        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
