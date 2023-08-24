package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Ticket")
@Getter
@Setter
public class Ticket {

    @Id
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="showing_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Showing showing;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="seat_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Seat seat;

    @Column(name = "price")
    private int price;
    @Column (name = "isDiscounted")
    private boolean isDiscounted;
    @Column(name = "isBooked")
    private boolean isBooked;

}