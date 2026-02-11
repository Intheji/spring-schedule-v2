package com.springschedule.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "댓글 내용이 없음 필수입니다")
    @Size(max = 200, message = "댓글을 최대 200자까지 가능한데")
    private String content;

    @NotNull(message = "작성자는 필수")
    private Long userId;
}
