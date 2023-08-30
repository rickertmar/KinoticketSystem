package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Cinema")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="location_id", referencedColumnName = "id")
    private LocationAddress locationAddress;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<CinemaHall> cinemaHallList;
}
