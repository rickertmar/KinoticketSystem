package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CinemaRepositoryTest {

    @Autowired
    private CinemaRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void checkIfCinemaExistsByName() {
        //given
        String cinemaName = "Test Cinema";
        Cinema cinema = new Cinema(
                1L,
                cinemaName,
                new LocationAddress(1L, "Test Street", "Test City", "Test Country", "12345"),
                new ArrayList<>()
        );
        underTest.save(cinema);

        //when
        Cinema expectedCinema = underTest.findByName(cinemaName).get();

        //then
        assertThat(expectedCinema).isEqualTo(cinema);
    }

    @Test
    void checkIfCinemaDoesNotExistsByName() {
        //given
        String cinemaName = "Test Cinema";

        //when
        Cinema expectedCinema = underTest.findByName(cinemaName).get();

        //then
        assertThat(expectedCinema).isNotNull();
    }
}