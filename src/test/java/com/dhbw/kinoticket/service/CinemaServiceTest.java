package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    @Mock private CinemaRepository cinemaRepository;
    @Mock private LocationAddressRepository locationAddressRepository;
    private CinemaService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CinemaService(cinemaRepository, locationAddressRepository);
    }

    @Test
    @Disabled
    void findAll() {
    }

    @Test
    @Disabled
    void findById() {
    }

    @Test
    @Disabled
    void findByName() {
    }


    @Test
    @Disabled
    void createCinema() {
    }

    @Test
    @Disabled
    void updateCinema() {
    }

    @Test
    @Disabled
    void deleteCinema() {
    }
}