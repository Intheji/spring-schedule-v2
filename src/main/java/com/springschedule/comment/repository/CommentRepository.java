package com.springschedule.comment.repository;

import com.springschedule.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 일정의 댓글을 생성일 오름차순으로 조회
    List<Comment> findAllBySchedule_IdOrderByCreatedAtAsc(Long scheduleId);

    // 일정 안에 댓글 단건 조회 쿼리메서드
    Optional<Comment> findByIdAndSchedule_Id(Long id, Long scheduleId);


    // group by로 일정 여러 개에 대한 댓글 개수를 조회
    @Query("""
        select c.schedule.id as scheduleId, count(c.id) as commentCount
        from Comment c
        where c.schedule.id in :scheduleIds
        group by c.schedule.id
    """)
    List<ScheduleCommentCount> countByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds);

    Long countBySchedule_Id(Long scheduleId);
}
