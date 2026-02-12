package com.springschedule.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateScheduleRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 30, message = "제목은 최대 30자까지 가능합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 200, message = "내용은 최대 200자까지 가능합니다")
    private String content;


}
