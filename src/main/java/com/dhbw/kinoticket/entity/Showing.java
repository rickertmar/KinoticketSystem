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

    private double seatPrice; // default price per seat

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="movie_id")
    private Movie movie; // running movie

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time; // date and time of the showing

    private String showingExtras; // extras like 2D or 3D

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "showing_seats",
            joinColumns = @JoinColumn(name = "showing_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> seats; // set of copied seats from the original cinema hall

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall cinemaHall;
}