package com.springschedule.schedule.service;

import com.springschedule.comment.repository.CommentRepository;
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
    private final CommentRepository commentRepository;


    // 일정을 생성
    @Transactional
    public ScheduleResponse save(CreateScheduleRequest request, Long loginUserId) {

        User user = getUserOrThrow(loginUserId);

        // 생성자 호출
        Schedule schedule = new Schedule(
                user,
                request.getTitle(),
                request.getContent()
        );

        scheduleRepository.save(schedule);
        return toScheduleResponse(schedule);
    }

    // 일정 단건 조회
    @Transactional(readOnly = true)
    public ScheduleResponse findOne(Long scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        return toScheduleResponse(schedule);
    }

    // 전체 일정 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponse> findAll(String authorName) {

        List<Schedule> schedules;

        if (authorName == null || authorName.isBlank()) {
            schedules = scheduleRepository.findAllByOrderByModifiedAtDesc();
        } else {
            schedules = scheduleRepository.findByUser_UserNameOrderByModifiedAtDesc(authorName);
        }

        List<ScheduleResponse> responses = new ArrayList<>();
        for (Schedule schedule : schedules) {
            responses.add(toScheduleResponse(schedule));
        }
        return responses;
    }

    // 일정 수정
    @Transactional
    public ScheduleResponse update(Long scheduleId, UpdateScheduleRequest request, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 작성자 맞는지 체크
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }

        schedule.update(request.getTitle(), request.getContent());
        return toScheduleResponse(schedule);
    }


    // 일정 삭제
    @Transactional
    public void delete(Long scheduleId, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 작성자 맞는지 체크
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }

        commentRepository.deleteAllByScheduleId(scheduleId);
        scheduleRepository.delete(schedule);
    }




    // 일정 조회하고 없으면 예외
    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 일정입니다")
        );
    }

    // entity를 응답 dto 변환
    private ScheduleResponse toScheduleResponse(Schedule schedule) {
        return new ScheduleResponse(
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
