package com.springschedule.comment.repository;

import com.springschedule.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 일정의 댓글을 생성일 오름차순으로 조회
    List<Comment> findAllByScheduleIdOrderByCreatedAtAsc(Long scheduleId);

    // 댓글 일괄 삭제 쿼리 메서드
    void deleteAllByScheduleId(Long scheduleId);

    // 일정 안에 댓글 단건 조회 쿼리메서드
    Optional<Comment> findByIdAndScheduleId(Long id, Long scheduleId);

}
