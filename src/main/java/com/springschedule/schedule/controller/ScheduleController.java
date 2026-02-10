package com.springschedule.schedule.controller;

import com.springschedule.schedule.dto.*;
import com.springschedule.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 생성
    @PostMapping
    public ResponseEntity<CreateScheduleResponse> create(
            @Valid @RequestBody CreateScheduleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(request));
    }

    // 일정 단건 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GetScheduleResponse> getOne(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findOne(scheduleId));
    }

    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<List<GetScheduleResponse>> getAll(
            @RequestParam(required = false) String authorName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findAll(authorName));
    }

    // 일정 수정
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<UpdateScheduleResponse> update(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.update(scheduleId, request));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long scheduleId
    ) {
        scheduleService.delete(scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
