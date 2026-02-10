package com.springschedule.schedule.repository;

import com.springschedule.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 쿼리 메서드로 전체 조회 - 수정일 내림차순
    List<Schedule> findAllByOrderByModifiedAtDesc();

    // 쿼리 메서드로 작성자명 조회 - 수정일 내림차순
    List<Schedule> findByUser_UserNameOrderByModifiedAtDesc(String userName);
}
