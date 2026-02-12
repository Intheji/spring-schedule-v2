package com.springschedule.schedule.controller;

import com.springschedule.common.exception.UnauthorizedException;
import com.springschedule.schedule.dto.*;
import com.springschedule.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
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
            throw new UnauthorizedException("님 로그인 해 주세요");
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
    public ResponseEntity<Page<ScheduleResponse>> getAll(
            @RequestParam(required = false) String userName,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "1 이상이 정상입니다") int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "일정이 한 개는 나와야죠")
            @Max(value = 30, message = "30개 이상은 지원 안 합니다") int size
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.findPage(userName, page, size));
    }

    // 일정 수정
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> update(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new UnauthorizedException("님 로그인 해 주세요");
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
            throw new UnauthorizedException("님 로그인 해 주세요");
        }
        scheduleService.delete(scheduleId, loginUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
