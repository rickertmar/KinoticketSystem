package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import com.dhbw.kinoticket.service.CinemaHallService;
import com.dhbw.kinoticket.service.CinemaService;
import com.dhbw.kinoticket.service.MovieService;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {CinemaControllerTest.class})
public class CinemaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private CinemaService cinemaService;
    @Mock
    private CinemaHallService cinemaHallService;
    @Mock
    private MovieService movieService;

    List<Cinema> cinemaList;
    Cinema cinema;
    CinemaHall cinemaHall;

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
        cinemaList.add(new Cinema(1L, "Test cinema 1", null, null, null));
        cinemaList.add(new Cinema(2L, "Test cinema 2", null, null, null));

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
        cinema = new Cinema(1L, "Test cinema 1", null, null, null);
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
        cinema = new Cinema(1L, "Test cinema 1", new LocationAddress(1L, "Test street", "Test city", "Test country", "12345"), null, null);
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
        cinema = new Cinema(id, "Test cinema 1", new LocationAddress(1L, "Test street", "Test city", "Test country", "12345"), null, null);
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
    void test_updateLocationAddress_ShouldUpdateLocationAddressOfCinema() throws Exception {
        // Arrange
        Long id = 1L;
        LocationAddress locationAddress = new LocationAddress(1L, "Test street", "Test city", "Test country", "12345");
        cinema = new Cinema(id, "Test cinema 1", locationAddress, null, null);

        // Mock
        when(cinemaService.updateLocationAddress(id, locationAddress)).thenReturn(cinema);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(locationAddress);

        // Act and Assert
        this.mockMvc.perform(put("/cinemas/{id}/location", id)
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.locationAddress.street").value("Test street"))
                .andDo(print());
    }

    // ----------------------------------------------------------------
    // CinemaHall Testing Methods
    // ----------------------------------------------------------------
    @Test
    @Order(7)
    void test_getCinemaHall_ShouldReturnCinemaHallById() throws Exception {
        // Arrange
        cinemaHall = new CinemaHall(1L, "Test cinemahall", null, null);
        Long cinemaId = 1L;
        Long cinemaHallId = 1L;

        // Mock
        when(cinemaHallService.getCinemaHallById(cinemaHallId)).thenReturn(cinemaHall);

        // Act and Assert
        this.mockMvc.perform(get("/cinemas/{cinemaId}/cinemahalls/{cinemaHallId}", cinemaId, cinemaHallId))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test cinemahall"))
                .andDo(print());
    }

    @Test
    @Order(8)
    void test_addCinemaHall_ShouldCreateCinemaHallAndAddItToCinema() throws Exception {
        // Arrange
        Long id = 1L;
        String name = "Test cinemahall";
        cinemaHall = new CinemaHall(id, name, null, null);

        // Mock
        when(cinemaHallService.createCinemaHallAndAddToCinema(id, name)).thenReturn(cinemaHall);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(name);

        // Act and Assert
        this.mockMvc.perform(post("/cinemas/{id}/cinemahalls", id)
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(9)
    void test_addSeatsToCinemaHall_ShouldCreateSeatListAndAddItToCinemaHall() throws Exception {
        // Arrange
        Long cinemaId = 1L;
        Long cinemaHallId = 1L;
        cinemaHall = new CinemaHall(cinemaHallId, "Test cinemahall", null, null);
        List<CreateSeatRequest> requestList = new ArrayList<>();
        requestList.add(new CreateSeatRequest('A', 1, 10, 20, true));
        List<Seat> seatList = cinemaHallService.convertSeatDTOsToSeats(requestList, cinemaHall);
        cinemaHall.setSeats(seatList);

        // Mock
        when(cinemaHallService.addSeatsToCinemaHall(cinemaHallId, requestList)).thenReturn(cinemaHall);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(requestList);

        // Act and Assert
        this.mockMvc.perform(post("/cinemas/{cinemaId}/cinemahalls/{cinemaHallId}/seats", cinemaId, cinemaHallId)
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(10)
    void test_updateCinemaHallSeats_ShouldUpdateSeatListOfCinemaHall() throws Exception {
        // Arrange
        Long cinemaId = 1L;
        Long cinemaHallId = 1L;
        String name = "Test cinemahall";
        cinemaHall = new CinemaHall(cinemaHallId, name, null, null);
        List<CreateSeatRequest> requestList = new ArrayList<>();
        requestList.add(new CreateSeatRequest('A', 1, 10, 20, true));
        List<Seat> seatList = cinemaHallService.convertSeatDTOsToSeats(requestList, cinemaHall);
        cinemaHall.setSeats(seatList);

        // Mock
        when(cinemaHallService.updateSeatsOfCinemaHall(cinemaHallId, name, requestList)).thenReturn(cinemaHall);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonbody = mapper.writeValueAsString(requestList);

        // Act and Assert
        this.mockMvc.perform(put("/cinemas/{cinemaId}/cinemahalls/{cinemaHallId}/seats", cinemaId, cinemaHallId)
                        .param("New_Name", name)
                        .content(jsonbody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(11)
    void test_deleteCinemaHall_ShouldDeleteCinemaHallById() throws Exception {
        // Arrange
        Long cinemaId = 1L;
        Long cinemaHallId = 1L;

        // Mock the deleteCinema() method
        doNothing().when(cinemaHallService).deleteCinemaHall(cinemaHallId);

        // Act and Assert
        this.mockMvc.perform(delete("/cinemas/{cinemaId}/cinemahalls/{cinemaHallId}", cinemaId, cinemaHallId))
                .andExpect(status().isOk())
                .andDo(print());
    }


    // ----------------------------------------------------------------
    // Movie Testing Methods
    // ----------------------------------------------------------------
    @Test
    @Order(12)
    public void test_GetAllMovies_ShouldReturnAllMovies() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(
                new Movie(1L, "Movie 1", FSK.FSK12, "Description 1", 2021, "Genre 1", "Director 1", 1, "120 minutes", "Country 1", "image1.jpg", "Actor 1", null),
                new Movie(2L, "Movie 2", FSK.FSK16, "Description 2", 2022, "Genre 2", "Director 2", 2, "110 minutes", "Country 2", "image2.jpg", "Actor 2", null)
        );

        // Mock
        when(movieService.getAllMovies()).thenReturn(movies);

        // Act and Assert
        mockMvc.perform(get("/cinemas/movies", 1L))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Movie 2"))
                .andDo(print());
    }

    @Test
    @Order(13)
    public void test_GetMovieById_ShouldReturnMovieById() throws Exception {
        // Arrange
        Movie movie = new Movie(1L, "Movie 1", FSK.FSK12, "Description 1", 2021, "Genre 1", "Director 1", 1, "120 minutes", "Country 1", "image1.jpg", "Actor 1", null);

        // Mock
        when(movieService.getMovieById(1L)).thenReturn(movie);

        // Act and Assert
        mockMvc.perform(get("/cinemas/movies/{id}", 1L, 1L))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Movie 1"))
                .andDo(print());
    }

    @Test
    @Order(14)
    public void test_AddMovieToCinema_ShouldCreateMovieAndAddToCinema() throws Exception {
        // Arrange
        Movie movie = new Movie(1L, "Movie 1", FSK.FSK12, "Description 1", 2021, "Genre 1", "Director 1", 1, "120 minutes", "Country 1", "image1.jpg", "Actor 1", null);

        // Mock
        when(movieService.addMovieToCinema(1L, movie)).thenReturn(movie);

        // Act and Assert
        mockMvc.perform(post("/cinemas/{cinemaId}/movies", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Movie 1"))
                .andDo(print());
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(15)
    public void test_RemoveMovieById_ShouldDeleteAndRemoveMovie() throws Exception {
        // Act and Assert
        mockMvc.perform(delete("/cinemas/movies/{movieId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted."));

        // Verify that the movieService.removeMovie() method was called with the correct movieId
        verify(movieService, times(1)).removeMovie(1L);
    }
}