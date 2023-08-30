package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int seatRow;
    private int number;
    private int xLoc; //relative X-Location in theatre
    private int yLoc; //relative Y-Location in theatre
    private boolean isBlocked;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private CinemaHall cinemaHall;
}
