package com.springschedule.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequest {

    @NotBlank(message = "댓글을 적어 주세염")
    @Size(max = 200, message = "댓글은 200자까지만 가능함 너무 많이 씀")
    private String content;
}
