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
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;



    @Transactional
    public CommentResponse save(Long scheduleId, CreateCommentRequest request, Long loginUserId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        User user = getUserOrThrow(loginUserId);

        Comment comment = new Comment(request.getContent(), schedule, user);
        Comment saved = commentRepository.save(comment);

        return  toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAllBySchedule(Long scheduleId) {
        List<Comment> comments = commentRepository.findAllByScheduleIdOrderByCreatedAtAsc(scheduleId);

        List<CommentResponse> responses = new ArrayList<>();
        for (Comment comment : comments) {
            responses.add(toResponse(comment));
        }
        return responses;
    }

    @Transactional
    public CommentResponse update(Long scheduleId, Long commentId, UpdateCommentRequest request, Long loginUserId) {
        Comment comment = getCommentOrThrow(scheduleId, commentId);

        if (!comment.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }

        comment.update(request.getContent());
        return toResponse(comment);
    }

    @Transactional
    public void delete(Long scheduleId, Long commentId, Long loginUserId) {
        Comment comment = getCommentOrThrow(scheduleId, commentId);

        if (!comment.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("님 권한 없음");
        }

        commentRepository.delete(comment);
    }



    private Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("일정이 없는데요?")
        );
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 없는데요?")
        );
    }

    private Comment getCommentOrThrow(Long scheduleId, Long commentId) {
        return commentRepository.findByIdAndScheduleId(commentId, scheduleId).orElseThrow(
                () -> new IllegalStateException("댓글이 없는데요?")
        );
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUserName(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
