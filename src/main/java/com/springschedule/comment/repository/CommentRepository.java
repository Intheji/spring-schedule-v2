package com.springschedule.comment.repository;

import com.springschedule.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 일정의 댓글을 생성일 오름차순으로 조회
    List<Comment> findAllByScheduleIdOrderByCreatedAtAsc(Long scheduleId);

}
