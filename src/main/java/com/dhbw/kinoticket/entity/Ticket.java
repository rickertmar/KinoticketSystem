package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="seat_id")
    private Seat seat;

    private double price;
    private Discount discount;
    private boolean isValid;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;


}