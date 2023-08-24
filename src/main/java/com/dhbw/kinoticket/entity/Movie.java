package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Movie")
@Getter
@Setter
public class Movie {

    @Id
    private Long id;
    @Column(name = "title")
    private String title;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "fsk")
    private FSK fsk;
    @Column(name = "description")
    private String description;
}
