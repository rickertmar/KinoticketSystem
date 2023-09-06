package com.dhbw.kinoticket.entity;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="movie_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Movie movie;

    private LocalDateTime time;

    private int cinemaHall;

    @ManyToMany
    Set<Seat> freeSeats;
    @ManyToMany
    Set<Seat> blockedSeats;


}