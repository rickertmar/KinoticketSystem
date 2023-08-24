package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Refund")
@Getter
@Setter
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refundAmount")
    private double amount;
    @Column(name = "refundDate")
    private LocalDateTime refundDate;
    @Column(name = "reason")
    private String reason;

    @OneToMany(mappedBy = "refund", cascade = CascadeType.ALL) // relation to tickets
    private List<Ticket> refundTickets;
}
