package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "User")
@Getter
@Setter
public class User {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String eMail;
    @Column(name = "password")
    private String password;
}