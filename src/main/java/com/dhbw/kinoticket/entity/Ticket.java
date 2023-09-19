package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


    @ManyToOne
    @JoinColumn(name="seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private Discount discount;

    private boolean isValid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;


}