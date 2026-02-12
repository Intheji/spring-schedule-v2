package com.springschedule.schedule.repository;

import com.springschedule.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 쿼리 메서드로 전체 조회 - 수정일 내림차순
//    List<Schedule> findAllByOrderByModifiedAtDesc();

    // 쿼리 메서드로 작성자명 조회 - 수정일 내림차순
//    List<Schedule> findByUser_UserNameOrderByModifiedAtDesc(String userName);

    // 페이징 버전
//    Page<Schedule> findByUser_UserName(String userName, Pageable pageable);

    // 삭제 안 된 것만 조회하는 메서드
    Page<Schedule> findAllByDeletedAtIsNull(Pageable pageable);
    Page<Schedule> findByUser_UserNameAndDeletedAtIsNull(String userName, Pageable pageable);
    Optional<Schedule> findByIdAndDeletedAtIsNull(Long id);
}
