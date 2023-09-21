package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.Showing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowingRepository extends JpaRepository<Showing, Long> {
}
