package com.springschedule.user.repository;

import com.springschedule.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
