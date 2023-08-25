package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
