package com.springschedule.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateScheduleRequest {
    private String title;
    private String content;
    private String authorName;
}
