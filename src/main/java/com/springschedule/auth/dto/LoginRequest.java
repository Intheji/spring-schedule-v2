package com.springschedule.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일은 필수입니당")
    @Email(message = "이메일 형식이 올바르지 않아요 똑바로 작성해 보세요")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니당")
    private String password;
}
