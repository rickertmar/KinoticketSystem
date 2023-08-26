package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.Cinema;
import org.springframework.data.repository.CrudRepository;

public interface CinemaRepository extends CrudRepository<Cinema, Long> {
    Cinema findByName(String name);
}
