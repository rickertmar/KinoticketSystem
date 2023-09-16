package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Showing")
public class Showing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id")
    private Movie movie;

    private LocalDateTime time;
    private String showingExtras;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "showing_free_seats",
            joinColumns = @JoinColumn(name = "showing_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> freeSeats;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "showing_blocked_seats",
            joinColumns = @JoinColumn(name = "showing_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> blockedSeats;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall cinemaHall;
}