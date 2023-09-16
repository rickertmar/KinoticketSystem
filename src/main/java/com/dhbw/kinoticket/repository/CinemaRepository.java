package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}
