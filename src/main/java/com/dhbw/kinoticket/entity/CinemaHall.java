package com.dhbw.kinoticket.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CinemaHall")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "cinemaHall", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
