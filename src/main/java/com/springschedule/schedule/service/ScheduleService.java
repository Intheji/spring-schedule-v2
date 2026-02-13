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


    // 일정 생성
    @Transactional
    public ScheduleResponse save(CreateScheduleRequest request, Long loginUserId) {

        User user = getUserOrThrow(loginUserId);

        Schedule schedule = new Schedule(
                user,
                request.getTitle(),
                request.getContent()
        );

        scheduleRepository.save(schedule);
        return toScheduleResponse(schedule);
    }

    // 일정 단건 조회: soft delete되지 않은 일정만 조회해서 반환
    public ScheduleResponse findOne(Long scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        return toScheduleResponse(schedule);
    }

    // 일정 목록 조회
    public Page<ScheduleResponse> findPage(String userName, int page, int size) {
        // page 1부터를 0으로 변환하고 정렬 조건 적용
        PageRequest pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "modifiedAt")
        );

        // 작성자 필터가 없으면 전체 조회, 있으면 작성자 이름으로 필터링
        Page<Schedule> schedulesPage = (userName == null || userName.isBlank())
                ? scheduleRepository.findAllByDeletedAtIsNull(pageable)
                : scheduleRepository.findByUser_UserNameAndDeletedAtIsNull(userName, pageable);

        // 현재 페이지에 포함된 일정 ID 목록 추출
        List<Long> scheduleIds = schedulesPage.getContent().stream()
                .map(Schedule::getId)
                .toList();

        // 일정별 댓글 개수를 집계해서 Map으로 변환
        Map<Long, Long> commentCountMap = scheduleIds.isEmpty()
                ? Map.of()
                : commentRepository.countByScheduleIds(scheduleIds).stream()
                .collect(Collectors.toMap(
                        ScheduleCommentCount::getScheduleId,
                        ScheduleCommentCount::getCommentCount
                ));

        // 댓글 개수 포함
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


    // 일정 삭제(soft delete 적용, deleteAt에 삭제 시간 저장)
    @Transactional
    public void delete(Long scheduleId, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);

        // 작성자 맞는지 체크
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }
        schedule.softDelete();
    }



    // 일정 조회하고 없으면 예외
    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findByIdAndDeletedAtIsNull(scheduleId).orElseThrow(
                () -> new IllegalStateException("일정이 없는데요..? 축하합니다")
        );
    }

    // 페이징 응답을 변환
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

    // 유저 조회: 작성자가 없으면 404 예외 처리
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 없음..")
        );
    }
}
