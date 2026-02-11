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
    public ResponseEntity<ScheduleResponse> create(
            @Valid @RequestBody CreateScheduleRequest request,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(request, loginUserId));
    }

    // 일정 단건 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getOne(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findOne(scheduleId));
    }

    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAll(
            @RequestParam(required = false) String authorName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findAll(authorName));
    }

    // 일정 수정
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> update(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.update(scheduleId, request, loginUserId));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long scheduleId,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        scheduleService.delete(scheduleId, loginUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
