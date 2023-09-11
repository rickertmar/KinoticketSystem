package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import com.dhbw.kinoticket.service.CinemaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {CinemaControllerTest.class})
class CinemaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private CinemaService cinemaService;

    List<Cinema> cinemaList;
    Cinema cinema;

    @InjectMocks
    private CinemaController cinemaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cinemaController).build();
    }

    @Test
    @Order(1)
    void test_GetAllCinemas_ShouldReturnAllCinemas() throws Exception {
        // Arrange
        cinemaList = new ArrayList<>();
        cinemaList.add(new Cinema(1L, "Test cinema 1", null, null));
        cinemaList.add(new Cinema(2L, "Test cinema 2", null, null));

        // Mock
        when(cinemaService.getAllCinemas()).thenReturn(cinemaList);

        // Act and Assert
        this.mockMvc.perform(get("/cinemas"))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    @Order(2)
    void test_GetCinemaById_ShouldReturnCinemaById() throws Exception {
        // Arrange
        cinema = new Cinema(1L, "Test cinema 1", null, null);
        Long cinemaId = 1L;

        // Mock
        when(cinemaService.getCinemaById(cinemaId)).thenReturn(cinema);

        // Act and Assert
        this.mockMvc.perform(get("/cinemas/{id}", cinemaId))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test cinema 1"))
                .andDo(print());
    }

    @Test
    @Order(3)
    void test_CreateCinema_ShouldCreateCinema() throws Exception {
        // Arrange
        cinema = new Cinema(1L, "Test cinema 1", new LocationAddress(1L, "Test street", "Test city", "Test country", "12345"), null);
        CreateCinemaRequest request = new CreateCinemaRequest("Test cinema 1", "Test street", "Test city", "Test country", "12345");

        // Mock
        when(cinemaService.createCinema(request)).thenReturn(cinema);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(request);

        // Act and Assert
        this.mockMvc.perform(post("/cinemas")
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(4)
    void test_UpdateCinema_ShouldUpdateCinemaById() throws Exception {
        // Arrange
        Long id = 1L;
        cinema = new Cinema(id, "Test cinema 1", new LocationAddress(1L, "Test street", "Test city", "Test country", "12345"), null);
        CreateCinemaRequest request = new CreateCinemaRequest("Test cinema 1", "Test street", "Test city", "Test country", "12345");

        // Mock
        when(cinemaService.updateCinema(id, request)).thenReturn(cinema);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(request);

        // Act and Assert
        this.mockMvc.perform(put("/cinemas/{id}", id)
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test cinema 1"))
                .andDo(print());
    }

    @Test
    @Order(5)
    void test_DeleteCinema_ShouldDeleteCinemaById() throws Exception {
        // Arrange
        Long cinemaId = 1L;

        // Mock the deleteCinema() method
        doNothing().when(cinemaService).deleteCinema(cinemaId);

        // Act and Assert
        this.mockMvc.perform(delete("/cinemas/{id}", cinemaId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(6)
    @Disabled
    void updateLocationAddress() {
    }

    @Test
    @Order(7)
    @Disabled
    void getCinemaHall() {
    }

    @Test
    @Order(8)
    @Disabled
    void addCinemaHall() {
    }

    @Test
    @Order(9)
    @Disabled
    void addSeatsToCinemaHall() {
    }

    @Test
    @Order(10)
    @Disabled
    void updateCinemaHallSeats() {
    }

    @Test
    @Order(11)
    @Disabled
    void deleteCinemaHall() {
    }
}