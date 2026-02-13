package com.springschedule.schedule.repository;

import com.springschedule.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // soft delete되지 않은 일정만 페이징 조회
    Page<Schedule> findAllByDeletedAtIsNull(Pageable pageable);

    // 작성자 이름으로 필터링
    Page<Schedule> findByUser_UserNameAndDeletedAtIsNull(String userName, Pageable pageable);

    // 일정 한 개를 soft delete 여부까지 포함해서 조회
    Optional<Schedule> findByIdAndDeletedAtIsNull(Long id);
}
