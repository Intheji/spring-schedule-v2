package com.springschedule.user.dto;

import java.time.LocalDateTime;


public record UserResponse (
    Long id,
    String userName,
    String email,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {}
