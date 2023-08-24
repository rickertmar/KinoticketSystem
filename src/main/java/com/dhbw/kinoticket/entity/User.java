package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="shippingLocation_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LocationAddress shippingLocation;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="billingLocation_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LocationAddress billingLocation;
}