package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.repository.CinemaHallRepository;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.service.CinemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


class CinemaControllerTest {

    private CinemaService cinemaService;
    private CinemaRepository cinemaRepository;
    private LocationAddressRepository locationAddressRepository;
    private CinemaHallRepository cinemaHallRepository;
    private CinemaController underTest;

    @BeforeEach
    void setUp() {
        underTest = new CinemaController(cinemaService, cinemaRepository, locationAddressRepository, cinemaHallRepository);
    }

    @Test
    @Disabled
    void createCinema() {
    }

    @Test
    @Disabled
    void getCinemaById() {
    }

    @Test
    @Disabled
    void deleteCinema() {
    }

    @Test
    @Disabled
    void updateLocationAddress() {
    }

    @Test
    @Disabled
    void addCinemaHall() {
    }

    @Test
    @Disabled
    void addSeatsToCinemaHall() {
    }

    @Test
    @Disabled
    void getCinemaHall() {
    }

    @Test
    @Disabled
    void updateCinemaHallSeats() {
    }

    @Test
    @Disabled
    void deleteCinemaHall() {
    }
}