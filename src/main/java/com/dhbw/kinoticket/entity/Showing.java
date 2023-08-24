package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "Showing")
@Getter
@Setter
public class Showing {

    @Id
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="movie_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Movie movie;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "cinemaHall")
    private int cinemaHall;
    @ManyToMany
    Set<Seat> freeSeats;
    @ManyToMany
    Set<Seat> blockedSeats;


}