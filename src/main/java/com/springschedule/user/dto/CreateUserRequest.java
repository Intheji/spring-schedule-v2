package com.springschedule.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "유저 이름은 필수입니다")
    @Size(max = 8, message = "유저 이름은 최대 8자까지 가능함")
    private String userName;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "email 형식이 틀렸음")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;
}
