package com.springschedule.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String userName,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {}
