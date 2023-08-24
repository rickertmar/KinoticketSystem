package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Cinema")
@Getter
@Setter
public class Cinema {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="locationAddress_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LocationAddress locationAddress;
}
