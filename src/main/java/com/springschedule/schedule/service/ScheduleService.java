package com.springschedule.schedule.service;

import com.springschedule.comment.repository.CommentRepository;
import com.springschedule.comment.repository.ScheduleCommentCount;
import com.springschedule.schedule.dto.*;
import com.springschedule.schedule.entity.Schedule;
import com.springschedule.schedule.repository.ScheduleRepository;
import com.springschedule.user.entity.User;
import com.springschedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public ScheduleResponse findOne(Long scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        return toScheduleResponse(schedule);
    }

    // 전체 일정 조회
    public Page<ScheduleResponse> findPage(String userName, int page, int size) {

        PageRequest pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "modifiedAt")
        );

        Page<Schedule> schedulesPage = (userName == null || userName.isBlank())
                ? scheduleRepository.findAllByDeletedAtIsNull(pageable)
                : scheduleRepository.findByUser_UserNameAndDeletedAtIsNull(userName, pageable);

        List<Long> scheduleIds = schedulesPage.getContent().stream()
                .map(Schedule::getId)
                .toList();

        Map<Long, Long> commentCountMap = scheduleIds.isEmpty()
                ? Map.of()
                : commentRepository.countByScheduleIds(scheduleIds).stream()
                .collect(Collectors.toMap(
                        ScheduleCommentCount::getScheduleId,
                        ScheduleCommentCount::getCommentCount
                ));

        return schedulesPage.map(schedule ->
                toScheduleResponse(schedule, commentCountMap.getOrDefault(schedule.getId(), 0L))
        );

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


    // 일정 삭제(soft delete 적용)
    @Transactional
    public void delete(Long scheduleId, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 작성자 맞는지 체크
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }

//        commentRepository.deleteAllBySchedule_Id(scheduleId);
//        scheduleRepository.delete(schedule);

        schedule.softDelete();
    }




    // 일정 조회하고 없으면 예외
    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findByIdAndDeletedAtIsNull(scheduleId).orElseThrow(
                () -> new IllegalStateException("일정이 없는데요..? 축하합니다")
        );
    }

    // 페이지 조회용
    private ScheduleResponse toScheduleResponse(Schedule schedule, Long commentCount) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getUser().getUserName(),
                commentCount,
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    // 단건 조회용
    private ScheduleResponse toScheduleResponse(Schedule schedule) {
        Long commentCount = commentRepository.countBySchedule_Id(schedule.getId());
        return toScheduleResponse(schedule, commentCount);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 없음..")
        );
    }
}
