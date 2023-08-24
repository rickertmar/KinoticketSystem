package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.entity.Worker;
import org.springframework.data.repository.CrudRepository;

public interface WorkerRepository extends CrudRepository<Worker, Long> {
    int countByUsername(String userName);

    Worker findByUsername(String userName);
}
