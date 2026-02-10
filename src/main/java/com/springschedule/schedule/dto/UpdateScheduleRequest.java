package com.springschedule.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateScheduleRequest {
    private String title;
    private String content;
}
