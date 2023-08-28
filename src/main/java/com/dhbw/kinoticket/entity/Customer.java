package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Customer")
@Getter
@Setter
public class Customer {

    @Id
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="shippingLocation_id", referencedColumnName = "id")
    private LocationAddress shippingLocation;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="billingLocation_id", referencedColumnName = "id")
    private LocationAddress billingLocation;
}
