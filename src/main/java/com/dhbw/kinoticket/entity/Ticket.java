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

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="showing_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Showing showing;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="seat_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Seat seat;

    private double price;
    private boolean isDiscounted;
    private boolean isBooked;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "refund_id") // relation to Refund
    private Refund refund;

}