package com.dhbw.kinoticket.dao;

import com.dhbw.kinoticket.entity.Cinema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCinemaRequest {
    @Valid
    private Cinema cinema;

    @Valid
    private LocationAddress locationAddress;
}
