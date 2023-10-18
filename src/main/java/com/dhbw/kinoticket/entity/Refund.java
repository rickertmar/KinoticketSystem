package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refund_amount")
    private double amount;

    @Column(name = "refund_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime refundDate; // date of refund

    @Column(name = "refund_reason")
    private String reason;

    @JsonIgnore
    @OneToOne
    private Reservation reservation; // associated reservation to refund

}
