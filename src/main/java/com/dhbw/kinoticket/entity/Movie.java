package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    @Enumerated(EnumType.ORDINAL)
    private FSK fsk;
    private String description;
    private int releaseYear;
    private String genres;
    private String director;
    private int runningWeek;
    private String runtime;
    private String releaseCountry;
    private String imageSrc;
    private String actors;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
