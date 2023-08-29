package com.dhbw.kinoticket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Seat")
@Getter
@Setter
public class Seat {

    @Id
    private Long id;
    @Column(name = "seatRow")
    private int seatRow;
    @Column(name = "number")
    private int number;

    @Column(name = "xLoc") //relative X-Location in theatre
    private int xLoc;

    @Column(name = "yLoc")//relative Y-Location in theatre
    private int yLoc;

}
