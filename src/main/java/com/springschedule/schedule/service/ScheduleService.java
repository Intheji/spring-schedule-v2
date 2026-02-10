package com.springschedule.schedule.service;

import com.springschedule.schedule.dto.*;
import com.springschedule.schedule.entity.Schedule;
import com.springschedule.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.springschedule.common.util.InputValidator.requireMaxLength;
import static com.springschedule.common.util.InputValidator.requireText;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // 일정 조회하고 없으면 예외
    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 일정입니다")
        );
    }

    // entity를 응답 dto 변환
    private GetScheduleResponse toGetScheduleResponse(Schedule schedule) {
        return new GetScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getAuthorName(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    // 일정을 생성
    @Transactional
    public CreateScheduleResponse save(CreateScheduleRequest request) {

        requireText(request.getTitle(), "일정 제목");
        requireMaxLength(request.getTitle(), "일정 제목", 30);
        requireText(request.getTitle(), "일정 내용");
        requireMaxLength(request.getTitle(), "일정 내용", 200);
        requireText(request.getAuthorName(), "작성자 이름");

        // 생성자 호출
        Schedule schedule = new Schedule(
                request.getTitle(),
                request.getContent(),
                request.getAuthorName()
        );

        // db 저장
        Schedule saved = scheduleRepository.save(schedule);

        return new CreateScheduleResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getAuthorName(),
                saved.getCreatedAt(),
                saved.getModifiedAt()
        );
    }

    // 일정 단건 조회
    @Transactional(readOnly = true)
    public GetScheduleResponse findOne(Long scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        return toGetScheduleResponse(schedule);
    }

    // 전체 일정 조회
    @Transactional(readOnly = true)
    public List<GetScheduleResponse> findAll(String authorName) {

        List<Schedule> schedules;

        if (authorName == null || authorName.isBlank()) {
            schedules = scheduleRepository.findAllByOrderByModifiedAtDesc();
        } else {
            schedules = scheduleRepository.findByAuthorNameOrderByModifiedAtDesc(authorName);
        }

        List<GetScheduleResponse> responses = new ArrayList<>();
        for (Schedule schedule : schedules) {
            responses.add(toGetScheduleResponse(schedule));
        }
        return responses;
    }

    // 일정 수정
    @Transactional
    public UpdateScheduleResponse update(Long scheduleId, UpdateScheduleRequest request) {

        requireText(request.getTitle(), "일정 제목");
        requireMaxLength(request.getTitle(), "일정 제목", 30);
        requireText(request.getTitle(), "일정 내용");
        requireMaxLength(request.getTitle(), "일정 내용", 200);

        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 더티 체킹
        schedule.update(request.getTitle(), request.getContent());

        return new UpdateScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getAuthorName(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    // 일정 삭제
    @Transactional
    public void delete(Long scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        scheduleRepository.delete(schedule);
    }
}
