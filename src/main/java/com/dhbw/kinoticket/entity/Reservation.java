package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity(name = "Reservation")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "total")
    private double total;
    @Column(name = "isPayed")
    private boolean isPayed;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Ticket> reservedTickets;
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Ticket> bookedTickets;

}
