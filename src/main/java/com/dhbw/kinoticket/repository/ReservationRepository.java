package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
