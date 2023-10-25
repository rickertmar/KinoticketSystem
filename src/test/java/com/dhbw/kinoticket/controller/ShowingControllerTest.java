package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.request.CreateShowingRequest;
import com.dhbw.kinoticket.response.ShowingByMovieResponse;
import com.dhbw.kinoticket.service.CinemaHallService;
import com.dhbw.kinoticket.service.ShowingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {ShowingControllerTest.class})
@AutoConfigureJsonTesters
class ShowingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private ShowingService showingService;

    @Mock
    private CinemaHallService cinemaHallService;

    @InjectMocks
    private ShowingController showingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(showingController).build();
    }


    @Test
    @Order(1)
    void test_GetAllShowings_ShouldReturnAllShowings() throws Exception {
        // Arrange
        List<Showing> showings = new ArrayList<>();
        showings.add(new Showing(1L, 12.70, null, null, "3D", null, null));
        showings.add(new Showing(2L, 9.70, null, null, "2D", null, null));

        // Mock
        when(showingService.getAllShowings()).thenReturn(showings);

        // Act and Assert
        mockMvc.perform(get("/showings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(showings.size()))
                .andDo(print());
    }

    @Test
    @Order(2)
    void test_GetShowingById_ShouldReturnShowingById() throws Exception {
        // Arrange
        Long showingId = 1L;
        Showing showing = new Showing(showingId, 12.70, null, null, "3D", null, null);

        // Mock
        when(showingService.getShowingById(showingId)).thenReturn(showing);

        // Act and Assert
        mockMvc.perform(get("/showings/{id}", showingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(showingId))
                .andDo(print());
    }

    @Test
    @Order(3)
    public void test_GetSeatsOfShowing_ShouldReturnSeatsOfShowing() throws Exception {
        // Mock the behavior of the showingService.getSeatsOfShowing() method
        Set<Seat> seats = new HashSet<>();
        seats.add(new Seat());
        when(showingService.getSeatsOfShowing(1L)).thenReturn(seats);

        // Perform the GET request to the endpoint
        mockMvc.perform(get("/showings/{id}/get-seats", 1L))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(4)
    public void test_GetSeatsOfShowing_EntityNotFoundException() throws Exception {
        // Mock the behavior of the showingService.getSeatsOfShowing() method to throw EntityNotFoundException
        when(showingService.getSeatsOfShowing(1L)).thenThrow(new EntityNotFoundException());

        // Perform the GET request to the endpoint
        mockMvc.perform(get("/showings/{id}/get-seats", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void test_GetSeatsOfShowing_Exception() throws Exception {
        // Mock the behavior of the showingService.getSeatsOfShowing() method to throw an exception
        when(showingService.getSeatsOfShowing(1L)).thenThrow(new RuntimeException());

        // Perform the GET request to the endpoint
        mockMvc.perform(get("/showings/{id}/get-seats", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(6)
    public void test_CreateShowing_WhenValidRequest_ThenReturnCreated() throws Exception {
        // Arrange
        CreateShowingRequest request = new CreateShowingRequest();
        request.setMovieId(1L);
        request.setCinemaHallId(1L);
        request.setTime(null);
        request.setShowingExtras("3D");
        Showing showing = new Showing(1L, 12.70, null, null, "3D", null, null);

        when(showingService.doesMovieExist(request.getMovieId())).thenReturn(true);
        when(cinemaHallService.doesCinemaHallExist(request.getCinemaHallId())).thenReturn(true);
        when(showingService.createShowing(request)).thenReturn(showing);

        // Act & Assert
        mockMvc.perform(post("/showings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(7)
    public void test_UpdateShowing_WhenValidRequest_ThenReturnOk() throws Exception {
        // Arrange
        Long id = 1L;
        CreateShowingRequest request = new CreateShowingRequest();
        request.setMovieId(1L);
        request.setCinemaHallId(1L);
        Showing showing = new Showing(1L, 12.70, null, null, "3D", null, null);

        // Mock
        when(showingService.updateShowing(id, request)).thenReturn(showing);

        // Act & Assert
        mockMvc.perform(put("/showings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(8)
    public void test_DeleteShowing_WhenValidRequest_ThenReturnOk() throws Exception {
        // Arrange
        Long showingId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/showings/{showingId}", showingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @Order(9)
    public void test_GetShowingsByMovieId_Success() throws Exception {
        Long movieId = 1L;
        List<ShowingByMovieResponse> showings = new ArrayList<>();
        showings.add(ShowingByMovieResponse.builder().build());
        showings.add(ShowingByMovieResponse.builder().build());

        when(showingService.getShowingsByMovieId(movieId)).thenReturn(showings);

        mockMvc.perform(get("/showings/get-by-movie-id/{movieId}", movieId))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(showings.size())))
                .andDo(print());
    }

    @Test
    @Order(10)
    public void test_GetShowingsByMovieId_NotFound() throws Exception {
        Long movieId = 1L;

        when(showingService.getShowingsByMovieId(movieId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/get-by-movie-id/{movieId}", movieId))
                .andExpect(status().isNotFound());
    }


    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}