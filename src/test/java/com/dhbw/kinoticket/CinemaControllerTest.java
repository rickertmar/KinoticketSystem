package com.dhbw.kinoticket;

import com.dhbw.kinoticket.controller.CinemaController;
import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.service.CinemaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
public class CinemaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private CinemaService cinemaService;

    @InjectMocks
    private CinemaController cinemaController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cinemaController).build();
    }


    private static final Long CINEMA_ID_FOUND = 1L;
    private static final Long CINEMA_ID_NOT_FOUND = 2L;

    @Test
    public void testGetCinemaById_CinemaFound() throws Exception {
        // Arrange
        Cinema expectedCinema = new Cinema(CINEMA_ID_FOUND, "Test Cinema", new LocationAddress(1L, "Teststreet 1", "Testcity 1", "Testcountry 1", "11111"), new ArrayList<>());
        when(cinemaService.findById(CINEMA_ID_FOUND)).thenReturn(expectedCinema);

        // Act and Assert using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/cinemas/{id}", CINEMA_ID_FOUND)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("Test Cinema"));
    }

    @Test
    public void testGetCinemaById_CinemaNotFound() throws Exception {
        // Arrange (continued)
        when(cinemaService.findById(CINEMA_ID_NOT_FOUND)).thenReturn(null);

        // Act and Assert using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/cinemas/{id}", CINEMA_ID_NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
