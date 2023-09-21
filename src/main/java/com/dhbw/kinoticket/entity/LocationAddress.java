package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LocationAddress")
public class LocationAddress {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String street;
    private String city;
    private String country;
    private String postcode;

}