package com.springschedule.schedule.dto;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        String title,
        String content,
        String userName,
        Long commentCount,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {}
