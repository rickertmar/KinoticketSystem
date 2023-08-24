package com.dhbw.kinoticket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "LocationAddress")
@Getter
@Setter
public class LocationAddress {

    @Id
    private Long id;
    @Column(name = "street")
    private String street;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "postcode")
    private int postalcode;
}