package com.springschedule.schedule.service;

import com.springschedule.schedule.dto.*;
import com.springschedule.schedule.entity.Schedule;
import com.springschedule.schedule.repository.ScheduleRepository;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;



    // 일정을 생성
    @Transactional
    public CreateScheduleResponse save(CreateScheduleRequest request) {

        User user = getUserOrThrow(request.getUserId());

        // 생성자 호출
        Schedule schedule = new Schedule(
                user,
                request.getTitle(),
                request.getContent()
        );

        // db 저장
        Schedule saved = scheduleRepository.save(schedule);

        return new CreateScheduleResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getUser().getUserName(),
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
            schedules = scheduleRepository.findByUser_UserNameOrderByModifiedAtDesc(authorName);
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

        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 더티 체킹
        schedule.update(request.getTitle(), request.getContent());

        return new UpdateScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getUser().getUserName(),
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
                schedule.getUser().getUserName(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 없음..")
        );
    }
}
