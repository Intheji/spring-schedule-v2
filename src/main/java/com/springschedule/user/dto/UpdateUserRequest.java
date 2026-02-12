package com.springschedule.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Pattern(regexp = "^(?!\\s*$).+", message = "유저 이름 공백 안 됨")
    @Size(max = 8, message = "유저 이름은 최대 8자까지 가능함")
    private String userName;

    @Pattern(regexp = "^(?!\\s*$).+", message = "이메일 공백 안 됨")
    @Email(message = "email 형식이 틀렸음")
    private String email;
}
