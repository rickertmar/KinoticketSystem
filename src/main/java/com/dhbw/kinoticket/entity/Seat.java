package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private char seatRow;
    private int number;
    private int xLoc; // relative X-Location in theatre
    private int yLoc; // relative Y-Location in theatre
    private boolean isBlocked; // indicates a temporary block
    private boolean isPermanentBlocked; // indicates a permanent block, e.g. when booked

    @Column(name = "blocked_at_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime blockedAtTimestamp; // timestamp when blocked

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private CinemaHall cinemaHall;

}
