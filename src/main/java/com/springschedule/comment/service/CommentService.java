package com.springschedule.comment.service;

import com.springschedule.comment.dto.CommentResponse;
import com.springschedule.comment.dto.CreateCommentRequest;
import com.springschedule.comment.dto.UpdateCommentRequest;
import com.springschedule.comment.entity.Comment;
import com.springschedule.comment.repository.CommentRepository;
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
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;



    // 댓글 생성: 일정 존재하는지 확인 후에 로그인 사용자를 작성자로 해서 댓글을 저장
    @Transactional
    public CommentResponse save(Long scheduleId, CreateCommentRequest request, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        User user = getUserOrThrow(loginUserId);

        Comment comment = new Comment(request.getContent(), schedule, user);
        return toResponse(commentRepository.save(comment));
    }

    // 댓글 목록을 시간 오름차순으로 조회
    public List<CommentResponse> findAllBySchedule(Long scheduleId) {
        getScheduleOrThrow(scheduleId);

        List<Comment> comments = commentRepository.findAllBySchedule_IdOrderByCreatedAtAsc(scheduleId);

        List<CommentResponse> responses = new ArrayList<>();
        for (Comment comment : comments) {
            responses.add(toResponse(comment));
        }
        return responses;
    }

    // 댓글 수정: 작성자만 내용 수정 가능
    @Transactional
    public CommentResponse update(Long scheduleId, Long commentId, UpdateCommentRequest request, Long loginUserId) {
        getScheduleOrThrow(scheduleId);
        Comment comment = getCommentOrThrow(scheduleId, commentId);
        validateOwner(comment.getUser().getId(), loginUserId);
        comment.update(request.getContent());
        return toResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long scheduleId, Long commentId, Long loginUserId) {
        getScheduleOrThrow(scheduleId);
        Comment comment = getCommentOrThrow(scheduleId, commentId);
        validateOwner(comment.getUser().getId(), loginUserId);
        commentRepository.delete(comment);
    }




    // 일정 조회: soft delete되지 않은 일정만 허용 없으면 예외
    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findByIdAndDeletedAtIsNull(scheduleId).orElseThrow(
                () -> new IllegalStateException("일정이 없는데요?")
        );
    }

    // 유저 조회
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 없는데요?")
        );
    }

    // 댓글 조회
    private Comment getCommentOrThrow(Long scheduleId, Long commentId) {
        return commentRepository.findByIdAndSchedule_Id(commentId, scheduleId).orElseThrow(
                () -> new IllegalStateException("댓글이 없는데요?")
        );
    }

    // 댓글 응답 dto 반환
    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUserName(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }

    // 작성자 검증
    private void validateOwner(Long ownerId, Long loginUserId) {
        if (!ownerId.equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }
    }

}
