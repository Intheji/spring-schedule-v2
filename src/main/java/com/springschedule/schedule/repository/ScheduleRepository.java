package com.springschedule.schedule.repository;

import com.springschedule.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 쿼리 메서드로 전체 조회 - 수정일 내림차순
//    List<Schedule> findAllByOrderByModifiedAtDesc();

    // 쿼리 메서드로 작성자명 조회 - 수정일 내림차순
//    List<Schedule> findByUser_UserNameOrderByModifiedAtDesc(String userName);

    // 페이징 버전
    Page<Schedule> findByUser_UserName(String userName, Pageable pageable);
}
