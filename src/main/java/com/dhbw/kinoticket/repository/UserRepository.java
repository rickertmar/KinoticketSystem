package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    int countByEmail(String email);

    User findByEmail(String email);
}
